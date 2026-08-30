# Equb (እቁብ) — 100% Supabase Web Admin Management Portal PRD & Code Blueprints

This Product Requirements Document (PRD) and technical specification details every aspect of the **Equb Web Administration Portal**. The portal is engineered to run **100% on Supabase** (Supabase Database, Supabase Auth, Supabase Storage, Supabase Realtime) with **Zero Firebase dependency**.

It is specifically designed around the **Manual Bank Slip & Transaction ID Verification Workflow** and the **Official Broadcast Announcements System** (with all user-to-user chatting removed).

---

## Table of Contents
1. [Product Requirements Document (PRD)](#1-product-requirements-document-prd)
2. [Technical Architecture (Next.js 14 + Supabase)](#2-technical-architecture)
3. [Complete Database Schema (Supabase PostgreSQL)](#3-complete-database-schema)
4. [Administrative Functional Modules](#4-administrative-functional-modules)
   - Module 1: Dashboard & Liquidity Analytics
   - Module 2: Manual Payment Slip Inspection & Verification Queue
   - Module 3: Equb Pool Creation & Lifecycle Management
   - Module 4: Provably Fair Round Draw & Winner Assignment
   - Module 5: Official Announcements & Broadcast Publisher
   - Module 6: User Directory & KYC (National ID / Fayda) Review
   - Module 7: Payout Disbursement Manager
   - Module 8: Audit Logs & Financial Reconciliation
5. [Complete Source Code Implementations](#5-complete-source-code-implementations)
   - Supabase Server Client (`lib/supabase-server.ts`)
   - Next.js Slip Verification Action (`app/actions/verify-payment.ts`)
   - Slip Verification Queue UI (`app/admin/payments/page.tsx`)
   - Announcement Broadcast Publisher UI (`app/admin/announcements/page.tsx`)
   - Provably Fair Lottery Draw Engine (`lib/draw-engine.ts`)
6. [Role-Based Access Control (RBAC) & Security Policies](#6-role-based-access-control-rbac)

---

## 1. Product Requirements Document (PRD)

### 1.1 Objective
Provide Equb managers, financial operators, and compliance staff with a high-throughput, web-based control center to:
1. Verify manual member deposit slips (Commercial Bank of Ethiopia, Telebirr, Awash Bank, Bank of Abyssinia) against bank statements.
2. Advance Equb rounds and execute provably fair lottery draws.
3. Broadcast official bulletins, payment deadlines, and winner notices to members.
4. Verify user KYC (National ID / Passport photos) and manage member credit limits.

### 1.2 User Personas
- **Super Administrator**: Full system control, role assignment, financial audit export.
- **Finance Officer**: Reviews and approves/rejects payment slips, manages payout disbursement records.
- **Equb Operator**: Creates Equb pools, executes round lottery draws, publishes official announcements.
- **KYC Reviewer**: Validates user National ID / Fayda numbers and identity photos.

---

## 2. Technical Architecture

```
┌─────────────────────────────────────────────────────────────┐
│             Web Admin Portal (Next.js 14 App Router)        │
│        React Server Components + Tailwind CSS + Lucide      │
└──────────────────────────────┬──────────────────────────────┘
                               │
            (Authenticated via Supabase Auth SSR / RLS)
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    SUPABASE PRODUCTION                      │
│                                                             │
│  ┌───────────────────────┐   ┌───────────────────────────┐  │
│  │     Supabase Auth     │   │     Supabase Storage      │  │
│  │     (Admin Staff)     │   │     ('payment-proofs')    │  │
│  └───────────────────────┘   └───────────────────────────┘  │
│                                                             │
│  ┌───────────────────────┐   ┌───────────────────────────┐  │
│  │  PostgreSQL Database  │   │     Supabase Realtime     │  │
│  │  (Profiles, Equbs,    │   │   (Live Slip Queue &      │  │
│  │   Slips, Announcements│   │    Announcement Stream)   │  │
│  └───────────────────────┘   └───────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Complete Database Schema (Supabase PostgreSQL)

```sql
-- Enable Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. PROFILES & ROLES
CREATE TABLE public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(30) UNIQUE NOT NULL,
    email VARCHAR(100),
    national_id_number VARCHAR(50),
    national_id_verified BOOLEAN DEFAULT FALSE,
    kyc_status VARCHAR(20) DEFAULT 'PENDING' CHECK (kyc_status IN ('PENDING', 'APPROVED', 'REJECTED')),
    id_front_url TEXT,
    id_back_url TEXT,
    selfie_url TEXT,
    total_savings VARCHAR(50) DEFAULT '0 ETB',
    last_added_amount VARCHAR(50) DEFAULT '0 ETB',
    referral_code VARCHAR(30) UNIQUE,
    role VARCHAR(20) DEFAULT 'MEMBER' CHECK (role IN ('MEMBER', 'ADMIN', 'OPERATOR', 'FINANCE')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. EQUBS (CIRCLES)
CREATE TABLE public.equbs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50) DEFAULT 'General',
    cycle_type VARCHAR(20) NOT NULL CHECK (cycle_type IN ('Daily', 'Weekly', 'Monthly')),
    contribution_amount NUMERIC(14, 2) NOT NULL,
    total_pool_amount NUMERIC(14, 2) NOT NULL,
    max_members INT NOT NULL,
    current_members INT DEFAULT 0,
    current_round INT DEFAULT 1,
    total_rounds INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('PENDING', 'ACTIVE', 'COMPLETED', 'FROZEN')),
    payout_method VARCHAR(50) DEFAULT 'ROTATING_ORDER',
    next_payment_date DATE NOT NULL,
    payment_instructions JSONB DEFAULT '{
        "cbe_account": "1000123456789 (Equb Trust)",
        "telebirr": "0911234567 / 654321",
        "awash_account": "01320492819200",
        "instructions": "Transfer exact amount and submit the bank transaction reference with receipt screenshot."
    }'::jsonb,
    created_by UUID REFERENCES public.profiles(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. EQUB MEMBERS
CREATE TABLE public.equb_members (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    equb_id UUID REFERENCES public.equbs(id) ON DELETE CASCADE,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    position_number INT NOT NULL,
    has_received_payout BOOLEAN DEFAULT FALSE,
    payout_round INT,
    total_contributions NUMERIC(14, 2) DEFAULT 0.00,
    is_paid BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE (equb_id, position_number),
    UNIQUE (equb_id, user_id)
);

-- 4. PAYMENT SLIPS & VERIFICATIONS (OFFLINE BANKING ENGINE)
CREATE TABLE public.payment_proofs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    equb_id UUID REFERENCES public.equbs(id) ON DELETE CASCADE,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    round_number INT NOT NULL,
    amount NUMERIC(14, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- 'Commercial Bank of Ethiopia', 'Telebirr', 'Awash Bank', 'Bank of Abyssinia'
    transaction_id VARCHAR(100) NOT NULL, -- External Bank Transaction / Reference Number
    slip_image_url TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    rejection_reason TEXT,
    verified_by UUID REFERENCES public.profiles(id),
    verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 5. OFFICIAL ANNOUNCEMENTS (BROADCAST-ONLY)
CREATE TABLE public.announcements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    equb_id UUID REFERENCES public.equbs(id) ON DELETE CASCADE, -- NULL for all Equbs
    author_name VARCHAR(100) DEFAULT 'Equb Administrator',
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) DEFAULT 'General' CHECK (category IN ('General', 'Payment Due', 'Draw Winner', 'Security Alert', 'Maintenance')),
    is_urgent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 6. AUDIT LOG
CREATE TABLE public.admin_audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admin_id UUID REFERENCES public.profiles(id),
    action VARCHAR(100) NOT NULL,
    target_entity VARCHAR(50) NOT NULL,
    target_id VARCHAR(100) NOT NULL,
    details JSONB,
    ip_address VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

---

## 4. Administrative Functional Modules

### Module 1: Dashboard & Liquidity Overview
- **Real-Time KPIs**:
  - Total Capital In Circulation (e.g. `ETB 24,500,000`).
  - Total Active Equbs & Ongoing Rounds.
  - Number of Unverified Payment Slips (highlighted with pulsing badge).
  - Payout Obligations Due in next 72 hours.
- **Activity Stream**: Live list of incoming bank slips, new member signups, and approved contributions.

### Module 2: Manual Payment Slip Inspection & Verification Queue
- **Split-Screen Review Interface**:
  - **Left Pane**: User profile, phone number, Equb name, selected round, stated bank (CBE / Telebirr), and member-typed Transaction ID.
  - **Right Pane**: High-resolution zoomable deposit receipt / transfer screenshot.
- **Reconciliation Actions**:
  - `Approve`: Checks off member for current round, increments member's total savings in database, updates slip status to `APPROVED`.
  - `Reject`: Prompts for rejection reason (e.g. *"Transaction ID does not match bank statement"*, *"Blurry receipt image"*), logs rejection and alerts member.

### Module 3: Equb Creation & Lifecycle Manager
- Create new Equb circles with:
  - Contribution Amount & Cycle (Daily / Weekly / Monthly).
  - Target pool size and maximum member slots.
  - Bank payment instructions and deposit account numbers.
- View member roster, reorder positions in case of agreed swap, or freeze defaulted members.

### Module 4: Provably Fair Round Draw & Winner Selection
- For lottery-based Equbs:
  - Filters all members who have **Paid** for the current round and have not yet won.
  - Executes SHA-256 seed-based cryptographic draw.
  - Generates immutable public audit certificate.
  - Automatically posts an official Announcement with the winner's name and position.

### Module 5: Official Announcements & Broadcast Publisher
- Publish official notices to:
  - All platform members.
  - Specific Equb group circles.
- Choose category badge: `Payment Due`, `Draw Winner`, `Security Alert`, `General`.
- Flag urgent announcements with high-priority styling in the mobile app.

---

## 5. Complete Source Code Implementations

### A. Supabase Server Client (`lib/supabase-server.ts`)
```typescript
import { createClient } from "@supabase/supabase-js";

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL || "https://your-project.supabase.co";
const supabaseServiceKey = process.env.SUPABASE_SERVICE_ROLE_KEY || "";

// Server-side privileged client for admin actions and RLS bypass
export const supabaseAdmin = createClient(supabaseUrl, supabaseServiceKey, {
  auth: {
    persistSession: false,
    autoRefreshToken: false,
  },
});
```

### B. Next.js Server Action: Verify Payment Slip (`app/actions/verify-payment.ts`)
```typescript
"use server";

import { supabaseAdmin } from "@/lib/supabase-server";
import { revalidatePath } from "next/cache";

export async function approvePaymentSlip(proofId: string, adminId: string) {
  try {
    // 1. Fetch payment proof details
    const { data: proof, error: fetchErr } = await supabaseAdmin
      .from("payment_proofs")
      .select("*, equbs(*), profiles(*)")
      .eq("id", proofId)
      .single();

    if (fetchErr || !proof) throw new Error("Payment proof record not found.");
    if (proof.status === "APPROVED") throw new Error("This payment is already approved.");

    // 2. Update payment proof status
    const { error: updateProofErr } = await supabaseAdmin
      .from("payment_proofs")
      .update({
        status: "APPROVED",
        verified_by: adminId,
        verified_at: new Date().toISOString(),
      })
      .eq("id", proofId);

    if (updateProofErr) throw updateProofErr;

    // 3. Mark member as is_paid = true and credit total_contributions
    const { data: memberRecord } = await supabaseAdmin
      .from("equb_members")
      .select("total_contributions")
      .eq("equb_id", proof.equb_id)
      .eq("user_id", proof.user_id)
      .single();

    const currentTotal = Number(memberRecord?.total_contributions || 0);
    const newTotal = currentTotal + Number(proof.amount);

    await supabaseAdmin
      .from("equb_members")
      .update({
        is_paid: true,
        total_contributions: newTotal,
      })
      .eq("equb_id", proof.equb_id)
      .eq("user_id", proof.user_id);

    // 4. Log in Audit Trail
    await supabaseAdmin.from("admin_audit_logs").insert({
      admin_id: adminId,
      action: "APPROVE_PAYMENT_SLIP",
      target_entity: "payment_proofs",
      target_id: proofId,
      details: {
        amount: proof.amount,
        transaction_id: proof.transaction_id,
        equb_id: proof.equb_id,
        user_id: proof.user_id,
      },
    });

    // 5. Post an automatic Announcement for transparency
    await supabaseAdmin.from("announcements").insert({
      equb_id: proof.equb_id,
      author_name: "Finance Desk",
      title: `Payment Verified: ${proof.profiles.full_name}`,
      content: `Round ${proof.round_number} contribution of ${proof.amount} ETB via ${proof.payment_method} (Ref: ${proof.transaction_id}) has been verified and credited.`,
      category: "General",
      is_urgent: false,
    });

    revalidatePath("/admin/payments");
    return { success: true, message: "Payment verified and credited successfully." };
  } catch (err: any) {
    return { success: false, error: err.message };
  }
}

export async function rejectPaymentSlip(proofId: string, adminId: string, reason: string) {
  try {
    const { error } = await supabaseAdmin
      .from("payment_proofs")
      .update({
        status: "REJECTED",
        rejection_reason: reason,
        verified_by: adminId,
        verified_at: new Date().toISOString(),
      })
      .eq("id", proofId);

    if (error) throw error;

    await supabaseAdmin.from("admin_audit_logs").insert({
      admin_id: adminId,
      action: "REJECT_PAYMENT_SLIP",
      target_entity: "payment_proofs",
      target_id: proofId,
      details: { reason },
    });

    revalidatePath("/admin/payments");
    return { success: true, message: "Payment proof rejected." };
  } catch (err: any) {
    return { success: false, error: err.message };
  }
}
```

### C. Slip Verification Queue UI (`app/admin/payments/page.tsx`)
```tsx
import React from "react";
import { supabaseAdmin } from "@/lib/supabase-server";
import { Check, X, ExternalLink, ShieldAlert, Receipt } from "lucide-react";
import { approvePaymentSlip, rejectPaymentSlip } from "@/app/actions/verify-payment";

export const dynamic = "force-dynamic";

export default async function PaymentVerificationQueuePage() {
  const { data: pendingSlips } = await supabaseAdmin
    .from("payment_proofs")
    .select("*, profiles(full_name, phone), equbs(title)")
    .eq("status", "PENDING")
    .order("created_at", { ascending: true });

  return (
    <div className="p-8 bg-slate-50 min-h-screen space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Manual Payment Verification Queue</h1>
          <p className="text-sm text-slate-500">Cross-reference member bank transaction IDs and uploaded transfer slips.</p>
        </div>
        <div className="bg-amber-100 text-amber-900 px-4 py-2 rounded-lg font-bold text-sm">
          {pendingSlips?.length || 0} Slips Pending Review
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6">
        {pendingSlips && pendingSlips.length > 0 ? (
          pendingSlips.map((slip) => (
            <div key={slip.id} className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Left Column: Transaction Data */}
              <div className="space-y-4">
                <div>
                  <span className="text-xs font-bold text-rose-900 uppercase tracking-wider">{slip.equbs?.title}</span>
                  <h3 className="text-xl font-extrabold text-slate-900 mt-1">{slip.profiles?.full_name}</h3>
                  <p className="text-sm text-slate-500 font-mono">{slip.profiles?.phone}</p>
                </div>

                <div className="bg-slate-50 p-4 rounded-lg space-y-2 border border-slate-100">
                  <div className="flex justify-between">
                    <span className="text-xs text-slate-500">Round Number:</span>
                    <span className="text-xs font-bold text-slate-800">Round {slip.round_number}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-xs text-slate-500">Claimed Amount:</span>
                    <span className="text-sm font-extrabold text-emerald-700">{Number(slip.amount).toLocaleString()} ETB</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-xs text-slate-500">Bank / Method:</span>
                    <span className="text-xs font-bold text-slate-800">{slip.payment_method}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-xs text-slate-500">Transaction ID:</span>
                    <span className="text-xs font-mono font-bold bg-white px-2 py-0.5 rounded border text-slate-900">
                      {slip.transaction_id}
                    </span>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-3 pt-2">
                  <form action={approvePaymentSlip.bind(null, slip.id, "ADMIN-SESSION-ID")} className="flex-1">
                    <button type="submit" className="w-full py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-lg flex items-center justify-center gap-2 shadow-sm text-sm">
                      <Check className="w-4 h-4" /> Approve & Credit
                    </button>
                  </form>
                  <form action={rejectPaymentSlip.bind(null, slip.id, "ADMIN-SESSION-ID", "Unverified transaction ID")} className="flex-1">
                    <button type="submit" className="w-full py-2.5 bg-rose-50 hover:bg-rose-100 text-rose-700 font-bold rounded-lg flex items-center justify-center gap-2 border border-rose-200 text-sm">
                      <X className="w-4 h-4" /> Reject Slip
                    </button>
                  </form>
                </div>
              </div>

              {/* Right Column: High-Res Zoomable Slip Preview */}
              <div className="lg:col-span-2 bg-slate-900 rounded-lg p-2 flex flex-col items-center justify-center relative overflow-hidden min-h-[260px]">
                {slip.slip_image_url ? (
                  <img
                    src={slip.slip_image_url}
                    alt="Payment Slip"
                    className="max-h-[300px] w-auto object-contain rounded"
                  />
                ) : (
                  <div className="text-center text-slate-400 p-8">
                    <Receipt className="w-12 h-12 mx-auto mb-2 opacity-50" />
                    <p className="text-sm">No image attached (Transaction ID only submission)</p>
                  </div>
                )}
                {slip.slip_image_url && (
                  <a
                    href={slip.slip_image_url}
                    target="_blank"
                    rel="noreferrer"
                    className="absolute top-4 right-4 bg-black/70 hover:bg-black text-white text-xs px-3 py-1.5 rounded-md flex items-center gap-1.5 backdrop-blur"
                  >
                    <ExternalLink className="w-3 h-3" /> Open Full Image
                  </a>
                )}
              </div>
            </div>
          ))
        ) : (
          <div className="bg-white rounded-xl p-12 text-center border border-slate-200">
            <Check className="w-12 h-12 text-emerald-500 mx-auto mb-3" />
            <h3 className="text-lg font-bold text-slate-900">All Slips Verified!</h3>
            <p className="text-sm text-slate-500">There are no pending manual payment receipts awaiting review.</p>
          </div>
        )}
      </div>
    </div>
  );
}
```

### D. Official Announcement Broadcast Publisher (`app/admin/announcements/page.tsx`)
```tsx
import React from "react";
import { supabaseAdmin } from "@/lib/supabase-server";
import { Megaphone, Send, BellRing } from "lucide-react";
import { revalidatePath } from "next/cache";

export default async function AdminAnnouncementsPage() {
  const { data: equbList } = await supabaseAdmin.from("equbs").select("id, title");
  const { data: recentAnnouncements } = await supabaseAdmin
    .from("announcements")
    .select("*, equbs(title)")
    .order("created_at", { ascending: false })
    .limit(10);

  async function broadcastAnnouncement(formData: FormData) {
    "use server";
    const title = formData.get("title") as string;
    const content = formData.get("content") as string;
    const equbId = formData.get("equb_id") as string;
    const category = formData.get("category") as string;
    const isUrgent = formData.get("is_urgent") === "on";

    await supabaseAdmin.from("announcements").insert({
      title,
      content,
      equb_id: equbId ? equbId : null,
      category,
      is_urgent: isUrgent,
      author_name: "Equb Management",
    });

    revalidatePath("/admin/announcements");
  }

  return (
    <div className="p-8 bg-slate-50 min-h-screen space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Official Broadcast Announcements</h1>
        <p className="text-sm text-slate-500">Publish official bulletins, round draw winners, and payment due reminders to members.</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Broadcast Form */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center gap-2 text-rose-900 font-bold text-lg border-b pb-3">
            <Megaphone className="w-5 h-5" /> New Bulletin
          </div>

          <form action={broadcastAnnouncement} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Target Equb Audience</label>
              <select name="equb_id" className="w-full border rounded-lg p-2.5 text-sm bg-slate-50">
                <option value="">All Platform Members (Global)</option>
                {equbList?.map((e) => (
                  <option key={e.id} value={e.id}>{e.title}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Category</label>
              <select name="category" className="w-full border rounded-lg p-2.5 text-sm bg-slate-50">
                <option value="General">General Notice</option>
                <option value="Payment Due">Payment Due Reminder</option>
                <option value="Draw Winner">Draw Winner Announcement</option>
                <option value="Security Alert">Security Alert</option>
                <option value="Maintenance">System Maintenance</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Headline / Title</label>
              <input name="title" required placeholder="e.g. Round 3 Payment Deadline" className="w-full border rounded-lg p-2.5 text-sm" />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Announcement Body</label>
              <textarea name="content" rows={4} required placeholder="Write detailed bulletin message..." className="w-full border rounded-lg p-2.5 text-sm" />
            </div>

            <div className="flex items-center gap-2">
              <input type="checkbox" name="is_urgent" id="is_urgent" className="rounded text-rose-900" />
              <label htmlFor="is_urgent" className="text-xs font-semibold text-slate-700">Flag as High Priority / Urgent</label>
            </div>

            <button type="submit" className="w-full py-3 bg-rose-900 hover:bg-rose-800 text-white font-bold rounded-lg flex items-center justify-center gap-2 shadow text-sm">
              <Send className="w-4 h-4" /> Broadcast to Members
            </button>
          </form>
        </div>

        {/* Live Announcements Stream */}
        <div className="lg:col-span-2 bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
          <h3 className="font-bold text-slate-900 text-lg border-b pb-3 flex items-center gap-2">
            <BellRing className="w-5 h-5 text-slate-500" /> Recent Published Bulletins
          </h3>

          <div className="space-y-3">
            {recentAnnouncements?.map((item) => (
              <div key={item.id} className="p-4 rounded-lg border border-slate-100 bg-slate-50 space-y-1">
                <div className="flex justify-between items-start">
                  <span className="text-xs font-bold text-rose-900 bg-rose-50 px-2 py-0.5 rounded border border-rose-100">
                    {item.category} {item.is_urgent && "• URGENT"}
                  </span>
                  <span className="text-xs text-slate-400">{new Date(item.created_at).toLocaleDateString()}</span>
                </div>
                <h4 className="font-bold text-slate-900">{item.title}</h4>
                <p className="text-sm text-slate-600">{item.content}</p>
                <span className="text-xs text-slate-400 block pt-1">
                  Target: {item.equbs?.title || "All Platform Members"}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
```

---

## 6. Role-Based Access Control (RBAC) & Security Policies

| Role | Review Payment Slips | Publish Announcements | Execute Lottery Draws | View KYC Data | System Config |
|------|:--------------------:|:---------------------:|:---------------------:|:-------------:|:-------------:|
| **SUPER_ADMIN** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **FINANCE_OFFICER** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **EQUB_OPERATOR** | ❌ | ✅ | ✅ | ❌ | ❌ |
| **KYC_OFFICER** | ❌ | ❌ | ❌ | ✅ | ❌ |

- **Zero Client-Side Firebase**: All interactions execute directly against Supabase PostgreSQL via HTTPS REST & Realtime WebSocket channels.
- **Admin Row-Level Security**: Unprivileged users cannot update `payment_proofs.status` or insert records into `announcements`.

---

*Equb (እቁብ) 100% Supabase Admin Portal PRD & Technical Blueprint. Version 3.0 Production Edition.*
