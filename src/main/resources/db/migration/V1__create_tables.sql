CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    monthly_paid BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE loans (
    id BIGSERIAL PRIMARY KEY,

    member_id BIGINT NOT NULL,

    remaining_principal NUMERIC(12,2) NOT NULL,

    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_loan_member
        FOREIGN KEY (member_id)
        REFERENCES members(id)
);

CREATE TABLE app_state (
    id BIGINT PRIMARY KEY,

    monthly_contribution NUMERIC(12,2) NOT NULL,

    loan_interest_rate NUMERIC(5,4) NOT NULL,

    total_savings NUMERIC(14,2) NOT NULL,

    total_savings_with_interest NUMERIC(14,2) NOT NULL,

    bank_balance NUMERIC(14,2) NOT NULL
);