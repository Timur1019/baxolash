-- Хранение только URL файлов (R2). Удаление колонок path.

-- evaluation_requests: report_file_path -> report_file_url
ALTER TABLE evaluation_requests ADD COLUMN IF NOT EXISTS report_file_url VARCHAR(1024);
UPDATE evaluation_requests SET report_file_url = report_file_path WHERE report_file_path IS NOT NULL;
ALTER TABLE evaluation_requests DROP COLUMN IF EXISTS report_file_path;

-- evaluation_requests: cadastral_document_path -> cadastral_document_url
ALTER TABLE evaluation_requests ADD COLUMN IF NOT EXISTS cadastral_document_url VARCHAR(1024);
UPDATE evaluation_requests SET cadastral_document_url = cadastral_document_path WHERE cadastral_document_path IS NOT NULL;
ALTER TABLE evaluation_requests DROP COLUMN IF EXISTS cadastral_document_path;

-- evaluation_request_documents: file_path -> file_url
ALTER TABLE evaluation_request_documents ADD COLUMN IF NOT EXISTS file_url VARCHAR(1024);
UPDATE evaluation_request_documents SET file_url = file_path WHERE file_path IS NOT NULL;
ALTER TABLE evaluation_request_documents DROP COLUMN IF EXISTS file_path;
