ALTER TABLE turkey_condition
    ADD CONSTRAINT conditional_group_same_user FOREIGN KEY
    (conditional_group, user)
    REFERENCES turkey_group (id, user);
    
ALTER TABLE turkey_condition
    ADD CONSTRAINT target_group_same_user FOREIGN KEY
    (target_group, user)
    REFERENCES turkey_group (id, user);
