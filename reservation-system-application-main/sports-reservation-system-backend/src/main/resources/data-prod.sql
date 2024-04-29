INSERT INTO app_user (enabled, locked, balance, email, first_name, last_name, password, role, id, has_daily_discount) VALUES (true, false, 0, 'admin@admin.com', 'admin', 'admin', '$2a$10$cUnuchdx2SVunhJt93xZ..GtkjWwpm8/NhwHrRhWJ/Oxdw2f.zKfO', 'ADMIN', gen_random_uuid(), true) ON CONFLICT DO NOTHING;

INSERT INTO location (id, name) VALUES (gen_random_uuid(), 'Modrý sál') ON CONFLICT DO NOTHING;
INSERT INTO location (id, name) VALUES (gen_random_uuid(), 'Červený sál') ON CONFLICT DO NOTHING;
INSERT INTO location (id, name) VALUES (gen_random_uuid(), 'Zelený sál') ON CONFLICT DO NOTHING;
INSERT INTO location (id, name) VALUES (gen_random_uuid(), 'Žlutý sál') ON CONFLICT DO NOTHING;
INSERT INTO location (id, name) VALUES (gen_random_uuid(), 'Tenisová hala') ON CONFLICT DO NOTHING;
INSERT INTO location (id, name) VALUES (gen_random_uuid(), 'Golfové hřiště') ON CONFLICT DO NOTHING;
