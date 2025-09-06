-- Foreign Key Constraints per FitTracker Database
-- Eseguito automaticamente da Spring Boot

-- Aggiungi Foreign Key per habits -> users
ALTER TABLE habits 
ADD CONSTRAINT fk_habit_user 
FOREIGN KEY (user_id) REFERENCES users(id) 
ON DELETE CASCADE;

-- Aggiungi Foreign Key per user_equipments -> users
ALTER TABLE user_equipments 
ADD CONSTRAINT fk_user_equipment_user 
FOREIGN KEY (user_id) REFERENCES users(id) 
ON DELETE CASCADE;

-- Aggiungi Foreign Key per user_equipments -> equipments
ALTER TABLE user_equipments 
ADD CONSTRAINT fk_user_equipment_equipment 
FOREIGN KEY (equipment_id) REFERENCES equipments(id) 
ON DELETE CASCADE;

-- Aggiungi Foreign Key per habit_completions -> habits
ALTER TABLE habit_completions 
ADD CONSTRAINT fk_habit_completion_habit 
FOREIGN KEY (habit_id) REFERENCES habits(id) 
ON DELETE CASCADE;

-- Aggiungi Foreign Key per habit_completions -> users
ALTER TABLE habit_completions 
ADD CONSTRAINT fk_habit_completion_user 
FOREIGN KEY (user_id) REFERENCES users(id) 
ON DELETE CASCADE;

-- Aggiungi indici per performance
CREATE INDEX IF NOT EXISTS idx_habits_user_id ON habits(user_id);
CREATE INDEX IF NOT EXISTS idx_user_equipments_user_id ON user_equipments(user_id);
CREATE INDEX IF NOT EXISTS idx_user_equipments_equipment_id ON user_equipments(equipment_id);
CREATE INDEX IF NOT EXISTS idx_habit_completions_habit_id ON habit_completions(habit_id);
CREATE INDEX IF NOT EXISTS idx_habit_completions_user_id ON habit_completions(user_id);
CREATE INDEX IF NOT EXISTS idx_habit_completions_date ON habit_completions(completion_date);

-- Aggiungi unique constraint per username
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Aggiungi unique constraint per evitare duplicati in user_equipments
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_equipment_unique 
ON user_equipments(user_id, equipment_id);