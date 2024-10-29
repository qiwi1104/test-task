CREATE TABLE IF NOT EXISTS wallets(
        id UUID NOT NULL,
        balance DECIMAL,
        CONSTRAINT wallets_pkey PRIMARY KEY (id)
);