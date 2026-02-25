-- =====================================================
-- Flyway V1: все таблицы приложения (users, contact_requests,
-- regions, districts, appraisal_purposes, evaluation_requests,
-- evaluation_request_documents, evaluation_request_fixed_asset_items)
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================================================
-- 1. Пользователи (users)
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- =====================================================
-- 2. Обращения с сайта (contact_requests)
-- =====================================================
CREATE TABLE IF NOT EXISTS contact_requests (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    subject VARCHAR(100),
    message TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- =====================================================
-- 3. Регионы (regions)
-- =====================================================
CREATE TABLE IF NOT EXISTS regions (
    id VARCHAR(36) PRIMARY KEY,
    name_uz VARCHAR(200) NOT NULL,
    name_ru VARCHAR(200),
    sort_order INT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- =====================================================
-- 4. Районы (districts)
-- =====================================================
CREATE TABLE IF NOT EXISTS districts (
    id VARCHAR(36) PRIMARY KEY,
    region_id VARCHAR(36) NOT NULL REFERENCES regions(id),
    name_uz VARCHAR(200) NOT NULL,
    name_ru VARCHAR(200),
    sort_order INT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_districts_region_id ON districts(region_id);

-- =====================================================
-- 5. Цели оценки (appraisal_purposes)
-- =====================================================
CREATE TABLE IF NOT EXISTS appraisal_purposes (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name_uz VARCHAR(255) NOT NULL,
    name_ru VARCHAR(255),
    name_en VARCHAR(255),
    sort_order INT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- =====================================================
-- 6. Заявки на оценку (evaluation_requests)
-- =====================================================
CREATE TABLE IF NOT EXISTS evaluation_requests (
    id VARCHAR(36) PRIMARY KEY,
    client_user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL,
    request_type VARCHAR(50) NOT NULL DEFAULT 'REAL_ESTATE',
    object_description TEXT,
    cost DECIMAL(19,2),
    report_file_name VARCHAR(255),
    report_file_path VARCHAR(500),
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    cadastral_number VARCHAR(100),
    appraisal_purpose VARCHAR(100),
    owner_phone VARCHAR(30),
    bank_employee_phone VARCHAR(30),
    borrower_inn VARCHAR(50),
    cadastral_document_path VARCHAR(500),
    cadastral_document_file_name VARCHAR(255),
    appraised_object_name VARCHAR(500),
    borrower_name VARCHAR(255),
    region_id VARCHAR(36) REFERENCES regions(id),
    district_id VARCHAR(36) REFERENCES districts(id),
    vehicle_type VARCHAR(100),
    tech_passport_number VARCHAR(100),
    license_plate VARCHAR(50),
    property_owner_name VARCHAR(255),
    object_address VARCHAR(500),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    location_address VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_evaluation_requests_client ON evaluation_requests(client_user_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_requests_status ON evaluation_requests(status);
CREATE INDEX IF NOT EXISTS idx_evaluation_requests_region ON evaluation_requests(region_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_requests_district ON evaluation_requests(district_id);

-- =====================================================
-- 7. Документы заявок на оценку (evaluation_request_documents)
-- =====================================================
CREATE TABLE IF NOT EXISTS evaluation_request_documents (
    id VARCHAR(36) PRIMARY KEY,
    evaluation_request_id VARCHAR(36) NOT NULL REFERENCES evaluation_requests(id),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    uploaded_by_id VARCHAR(36) REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_eval_docs_request ON evaluation_request_documents(evaluation_request_id);

-- =====================================================
-- 8. Позиции основных средств (evaluation_request_fixed_asset_items)
-- =====================================================
CREATE TABLE IF NOT EXISTS evaluation_request_fixed_asset_items (
    id VARCHAR(36) PRIMARY KEY,
    evaluation_request_id VARCHAR(36) NOT NULL REFERENCES evaluation_requests(id),
    asset_type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity DECIMAL(19,4) NOT NULL,
    unit_of_measurement VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_fixed_asset_items_request ON evaluation_request_fixed_asset_items(evaluation_request_id);
