-- Remove files column from submission table
ALTER TABLE public.submission DROP COLUMN IF EXISTS files; 