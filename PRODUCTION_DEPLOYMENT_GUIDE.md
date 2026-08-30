# Equb (እቁብ) — 100% Supabase Production Deployment & Live Operations Guide

This comprehensive document outlines the end-to-end technical roadmap, infrastructure setup, cloud configurations, manual bank transfer / slip verification workflow, Supabase storage policies, and Google Play Store publishing guidelines to operate this Android application **exclusively on Supabase (Zero Firebase)**.

---

## Table of Contents
1. [Architecture Overview & 100% Supabase Topology](#1-architecture-overview--100-supabase-topology)
2. [Supabase Production Cloud Setup (Zero Firebase)](#2-supabase-production-cloud-setup)
   - Supabase Project & Auth Configuration (SMS OTP / Magic Link)
   - Supabase PostgreSQL Complete Relational Schema
   - Row-Level Security (RLS) Policies
   - Supabase Storage Bucket Configuration (Payment Slips & National ID)
   - Supabase Realtime Announcements Engine
3. [Offline Bank Transfer & Slip Verification System](#3-offline-bank-transfer--slip-verification-system)
   - Manual Payment Workflow (CBE, Telebirr, Awash, Abyssinia)
   - Transaction ID & Deposit Slip Upload Flow
   - Admin Manual Slip Verification & Double-Check Protocol
4. [Announcements-Only Communication Architecture](#4-announcements-only-communication-architecture)
   - Admin Broadcast Bulletin System
   - Supabase Database Realtime Broadcast Channels
5. [SMS & National ID (Fayda) Verification](#5-sms--national-id-fayda-verification)
   - Supabase Twilio / Termii / Ethio Telecom SMS Provider
   - Fayda NIN Identity Verification
6. [Android App Production Build & Signing](#6-android-app-production-build--signing)
   - Keystore Generation & Secrets Setup
   - Proguard / R8 Optimization
   - Release App Bundle (AAB) Generation
7. [Google Play Console Publishing Checklist](#7-google-play-console-publishing-checklist)
   - Financial Disclosure & Privacy Policy
   - Store Graphics & Screen Requirements
   - Rollout Tracks (Internal, Closed Testing, Production)
8. [Database Maintenance, Backups & Security Runbook](#8-database-maintenance-backups--security-runbook)

---

## 1. Architecture Overview & 100% Supabase Topology

```
┌─────────────────────────────────────────────────────────────┐
│                    Mobile Client (Android)                   │
│      Kotlin + Jetpack Compose + Room Local Offline Cache    │
└──────────────────────────────┬──────────────────────────────┘
                               │
               (HTTPS REST / WebSocket Realtime)
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  SUPABASE CLOUD PLATFORM                    │
│                                                             │
│  ┌───────────────────────┐   ┌───────────────────────────┐  │
│  │     Supabase Auth     │   │     Supabase Storage      │  │
│  │   (Phone SMS OTP)     │   │   ('payment-receipts',    │  │
│  │                       │   │    'kyc-documents')       │  │
│  └───────────────────────┘   └───────────────────────────┘  │
│                                                             │
│  ┌───────────────────────┐   ┌───────────────────────────┐  │
│  │  Supabase PostgreSQL  │   │     Supabase Realtime     │  │
│  │  • Profiles & Equbs   │   │  • Broadcast Announcements│  │
│  │  • Manual Slip Table  │   │  • Round State Updates    │  │
│  │  • Ledger & Payouts   │   │  • Verification Alerts    │  │
│  └───────────────────────┘   └───────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              Next.js 14 Web Admin Portal                    │
│   • Manual Deposit Slip Inspection & Approval Queue         │
│   • Transaction Reference Reconciliation                   │
│   • Official Announcement Broadcaster                       │
│   • Provably Fair Round Draw Execution                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Supabase Production Cloud Setup

### A. Create and Configure Supabase Project
1. Navigate to [Supabase Dashboard](https://supabase.com/dashboard).
2. Create a new project named: `equb-production-db`.
3. Select your compute region (e.g., `EU Frankfurt` or `Middle East Bahrain` for low latency to East Africa).
4. Save your **Project URL** and **`anon` public key** in your project's `.env` and `BuildConfig`.

### B. Supabase Phone Auth Configuration (No Firebase)
1. Go to **Authentication** > **Providers** > **Phone**.
2. Enable Phone Authentication.
3. Select your SMS Gateway Provider:
   - **Twilio / Termii / MessageBird**: Enter Account SID, Auth Token, and Sender ID.
   - **Template**:
     `"Your Equb (እቁብ) verification code is: {{ .Code }}. Valid for 5 minutes."`
4. Set OTP Expiry to 300 seconds.

### C. Supabase Complete PostgreSQL Schema
Run the following SQL in your Supabase SQL Editor:

```sql
-- =========================================================
-- EQUB PLATFORM 100% SUPABASE PRODUCTION DATABASE SCHEMA
-- =========================================================

-- 1. Enable Required Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 2. USER PROFILES TABLE (Linked to auth.users)
CREATE TABLE IF NOT EXISTS public.profiles (
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
    role VARCHAR(20) DEFAULT 'MEMBER' CHECK (role IN ('MEMBER', 'ADMIN', 'OPERATOR')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. EQUB POOLS TABLE
CREATE TABLE IF NOT EXISTS public.equbs (
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
        "cbe_account": "1000123456789 (Equb Savings Trust)",
        "telebirr_merchant": "0911234567 / 654321",
        "awash_account": "01320492819200",
        "instructions": "Transfer exact contribution amount. Take a clear screenshot or photo of the bank slip and copy the Transaction Reference Number."
    }'::jsonb,
    created_by UUID REFERENCES public.profiles(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. EQUB MEMBERSHIP ROSTER
CREATE TABLE IF NOT EXISTS public.equb_members (
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

-- 5. MANUAL PAYMENT SLIPS & CONTRIBUTIONS TABLE
-- Users pay externally and upload Transaction ID + Slip Image
CREATE TABLE IF NOT EXISTS public.payment_proofs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    equb_id UUID REFERENCES public.equbs(id) ON DELETE CASCADE,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    round_number INT NOT NULL,
    amount NUMERIC(14, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- 'Commercial Bank of Ethiopia', 'Telebirr', 'Awash Bank', 'Bank of Abyssinia'
    transaction_id VARCHAR(100) NOT NULL, -- External Bank Transaction / Reference ID
    slip_image_url TEXT NOT NULL, -- Supabase Storage Public/Signed URL
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    rejection_reason TEXT,
    verified_by UUID REFERENCES public.profiles(id),
    verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 6. OFFICIAL ANNOUNCEMENTS TABLE (No peer chatting; Admin broadcast only)
CREATE TABLE IF NOT EXISTS public.announcements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    equb_id UUID REFERENCES public.equbs(id) ON DELETE CASCADE, -- NULL for platform-wide bulletins
    author_name VARCHAR(100) DEFAULT 'Equb Administrator',
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) DEFAULT 'General' CHECK (category IN ('General', 'Payment Due', 'Draw Winner', 'Security Alert', 'Maintenance')),
    is_urgent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 7. PAYOUT DISBURSEMENTS
CREATE TABLE IF NOT EXISTS public.payouts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    equb_id UUID REFERENCES public.equbs(id) ON DELETE CASCADE,
    winner_user_id UUID REFERENCES public.profiles(id),
    round_number INT NOT NULL,
    payout_amount NUMERIC(14, 2) NOT NULL,
    disbursement_method VARCHAR(50) NOT NULL,
    disbursement_reference VARCHAR(100),
    disbursement_slip_url TEXT,
    status VARCHAR(20) DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'PROCESSING', 'DISBURSED', 'FAILED')),
    disbursed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

### D. Supabase Storage Buckets & Policies
Create two storage buckets in the Supabase Dashboard:
1. `payment-proofs` (Public: false or restricted access)
2. `kyc-documents` (Public: false)

Deploy the following Storage Access Policies:
```sql
-- Allow authenticated users to upload their own payment slip
CREATE POLICY "Users can upload their payment slips"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (
    bucket_id = 'payment-proofs' AND 
    (storage.foldername(name))[1] = auth.uid()::text
);

-- Allow users to view their own payment slips and admins to view all
CREATE POLICY "Users and admins can view payment slips"
ON storage.objects FOR SELECT
TO authenticated
USING (
    bucket_id = 'payment-proofs' AND (
        (storage.foldername(name))[1] = auth.uid()::text OR
        EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role IN ('ADMIN', 'OPERATOR'))
    )
);
```

### E. Row-Level Security (RLS) Policies
```sql
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.equbs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.equb_members ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payment_proofs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.announcements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payouts ENABLE ROW LEVEL SECURITY;

-- Profiles: Anyone authenticated can read; only owner can update
CREATE POLICY "Public profile view" ON public.profiles FOR SELECT TO authenticated USING (true);
CREATE POLICY "Profile self update" ON public.profiles FOR UPDATE TO authenticated USING (auth.uid() = id);

-- Announcements: All members can read; Only Admins can insert/update
CREATE POLICY "All can read announcements" ON public.announcements FOR SELECT TO authenticated USING (true);
CREATE POLICY "Admins can manage announcements" ON public.announcements FOR ALL TO authenticated 
USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role IN ('ADMIN', 'OPERATOR')));

-- Payment Proofs: Users can view & submit their own; Admins can review all
CREATE POLICY "Users can submit payment proof" ON public.payment_proofs FOR INSERT TO authenticated 
WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can view own payment proof" ON public.payment_proofs FOR SELECT TO authenticated 
USING (auth.uid() = user_id OR EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role IN ('ADMIN', 'OPERATOR')));

CREATE POLICY "Admins can update payment proof" ON public.payment_proofs FOR UPDATE TO authenticated 
USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role IN ('ADMIN', 'OPERATOR')));
```

---

## 3. Offline Bank Transfer & Slip Verification System

Since automated in-app payment gateways are held, payments follow a reliable **External Transfer -> Slip Upload -> Admin Verification** pipeline:

1. **Member Step 1 (Offline Payment)**:
   - Member opens the Equb payment details screen.
   - Views the official bank details:
     - Commercial Bank of Ethiopia (CBE): `1000123456789`
     - Telebirr: `0911234567`
     - Awash Bank: `01320492819200`
   - Transfers the exact contribution amount (e.g. `5,000 ETB`).
2. **Member Step 2 (Submit Proof in App)**:
   - Selects Bank / Mobile Money service used.
   - Enters the **Transaction Reference ID** (e.g., `FT24083091823` or `TB-982312`).
   - Attaches a clear camera photo or screenshot of the deposit receipt.
   - Taps **"Submit Payment Proof"**.
3. **Admin Step 3 (Manual Verification on Web Portal)**:
   - Admin sees the pending submission in the web portal verification queue.
   - Cross-checks the Bank Transaction ID against official bank statement.
   - Inspects the uploaded image for alterations.
   - Clicks **"Approve & Credit Round"** or **"Reject with Reason"**.
4. **Member Step 4 (Automatic State Update)**:
   - Upon approval, Supabase updates `payment_proofs.status = 'APPROVED'`, credits `equb_members.total_contributions`, sets `is_paid = true`, and notifies the user.

---

## 4. Announcements-Only Communication Architecture

To maintain clarity and financial decorum:
- **No User-to-User Chatting**: Personal messaging between random users is completely disabled.
- **Broadcast Announcements**: Equb creators and Super Admins broadcast official notices:
  - *"Round 3 Draw completed: Congratulations to Winner Position #2!"*
  - *"Payment Deadline for Round 4 is Tuesday, Sep 2 at 5:00 PM."*
  - *"Maintenance notice: Server upgrade scheduled at midnight."*
- **Realtime Updates**: The Android app listens to Supabase Realtime channel `public:announcements` to display new bulletins with priority badges.

---

## 5. Android App Production Build & Signing

### A. Environment Configuration (`.env`)
```properties
SUPABASE_URL=https://your-production-subdomain.supabase.co
SUPABASE_ANON_KEY=sb_publishable_your_real_production_key_here
```

### B. Generate Production Upload Keystore
```bash
keytool -genkeypair -v -keystore equb-release-key.jks \
  -alias equb-upload -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=Equb, OU=Mobile, O=Equb Technologies, L=Addis Ababa, C=ET"
```

### C. Generate Release App Bundle (AAB)
```bash
gradle :app:bundleRelease
```
The production `.aab` file will be generated at:
`/app/build/outputs/bundle/release/app-release.aab`

---

## 6. Google Play Console Publishing Checklist

1. **Category**: Finance / Personal Financial Management.
2. **Offline Payment Disclosure**: Clearly describe that the app operates on an offline bank deposit verification model where users submit transfer receipts.
3. **Data Safety Form**: Declare collection of Phone Number, Name, Transaction Reference IDs, and Receipt Images (all stored securely in Supabase with RLS).
4. **App Access Credentials**: Provide a demo test phone number (e.g. `+251911000000`) with test OTP for Google review team.

---

*Production Roadmap for Equb (እቁብ). Powered exclusively by Supabase.*
