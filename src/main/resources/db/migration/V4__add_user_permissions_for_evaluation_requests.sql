-- Разрешения для редактирования и удаления заявок на оценку.
-- Админ назначает эти права клиентам и сотрудникам компании.
ALTER TABLE users ADD COLUMN IF NOT EXISTS can_edit_evaluation_requests BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE users ADD COLUMN IF NOT EXISTS can_delete_evaluation_requests BOOLEAN NOT NULL DEFAULT true;
