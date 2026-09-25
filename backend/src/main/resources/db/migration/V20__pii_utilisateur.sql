ALTER TABLE identites.utilisateur
    ALTER COLUMN prenom TYPE text,
    ALTER COLUMN nom TYPE text,
    ALTER COLUMN two_factor_secret TYPE text;
