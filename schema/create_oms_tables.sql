-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS oms_db;

-- Switch to the created database
USE oms_db;

-- Create table with hash sharding
CREATE TABLE IF NOT EXISTS public.oms_details (
    order_id BIGINT PRIMARY KEY USING HASH WITH (bucket_count=16),
    customer_id STRING,
    order_status STRING,
    total_amount STRING,
    item_details STRING,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    version INT
);

-- Index for filtering by order status
CREATE INDEX IF NOT EXISTS idx_oms_order_status ON public.oms_details (order_status);

-- Index for querying by customer
CREATE INDEX IF NOT EXISTS idx_oms_customer_id ON public.oms_details (customer_id);

-- Index for time-based queries
CREATE INDEX IF NOT EXISTS idx_oms_created_at ON public.oms_details (created_at);
