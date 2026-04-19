-- Seed known accounts
INSERT INTO account (id, account_number, owner_name, country, created_at, is_blacklisted)
VALUES
    ('acc-001', 'ACC-NL-001', 'Anant Patnaik', 'NL', CURRENT_TIMESTAMP, false),
    ('acc-002', 'ACC-NL-002', 'Jane Smith', 'NL', CURRENT_TIMESTAMP, false),
    ('acc-003', 'ACC-US-003', 'Bob Johnson', 'US', CURRENT_TIMESTAMP, false),
    ('acc-004', 'ACC-NG-004', 'Risk Account', 'NG', CURRENT_TIMESTAMP, true),
    ('acc-005', 'ACC-IN-005', 'Test User India', 'IN', CURRENT_TIMESTAMP, false);

-- Seed historical transactions for acc-001 (normal behaviour baseline)
INSERT INTO transaction (id, account_id, amount, currency, merchant, merchant_category, location_country, location_city, transaction_time, status, risk_score, flagged, flagged_reason, reviewed, review_outcome)
VALUES
    ('txn-h-001', 'acc-001', 45.00, 'EUR', 'Albert Heijn', 'GROCERY', 'NL', 'Amsterdam', DATEADD('DAY', -30, CURRENT_TIMESTAMP), 'COMPLETED', 5, false, null, false, null),
    ('txn-h-002', 'acc-001', 120.00, 'EUR', 'Bol.com', 'ECOMMERCE', 'NL', 'Amsterdam', DATEADD('DAY', -25, CURRENT_TIMESTAMP), 'COMPLETED', 5, false, null, false, null),
    ('txn-h-003', 'acc-001', 300.00, 'EUR', 'KLM Airlines', 'TRAVEL', 'NL', 'Amsterdam', DATEADD('DAY', -20, CURRENT_TIMESTAMP), 'COMPLETED', 10, false, null, false, null),
    ('txn-h-004', 'acc-001', 85.00, 'EUR', 'HEMA', 'RETAIL', 'NL', 'Utrecht', DATEADD('DAY', -15, CURRENT_TIMESTAMP), 'COMPLETED', 5, false, null, false, null),
    ('txn-h-005', 'acc-001', 200.00, 'EUR', 'NS Trains', 'TRAVEL', 'NL', 'Rotterdam', DATEADD('DAY', -10, CURRENT_TIMESTAMP), 'COMPLETED', 8, false, null, false, null),
    ('txn-h-006', 'acc-002', 5000.00, 'EUR', 'Wire Transfer', 'TRANSFER', 'US', 'New York', DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'COMPLETED', 45, true, 'NEW_LOCATION,LARGE_AMOUNT', true, 'LEGITIMATE'),
    ('txn-h-007', 'acc-003', 250.00, 'USD', 'Amazon', 'ECOMMERCE', 'US', 'Seattle', DATEADD('DAY', -3, CURRENT_TIMESTAMP), 'COMPLETED', 5, false, null, false, null);

-- Seed blacklisted countries
INSERT INTO blacklisted_country (country_code, country_name, reason)
VALUES
    ('NG', 'Nigeria', 'High fraud rate'),
    ('KP', 'North Korea', 'Sanctioned'),
    ('IR', 'Iran', 'Sanctioned'),
    ('MM', 'Myanmar', 'High fraud rate');