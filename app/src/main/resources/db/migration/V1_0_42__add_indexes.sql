CREATE INDEX IF NOT EXISTS idx_terminal_acquirer_params_id
ON terminal_acquirer_params_operations_mapping (terminal_acquirer_params_id);

CREATE INDEX IF NOT EXISTS idx_terminal_group_id ON terminal (group_id);

CREATE INDEX IF NOT EXISTS idx_terminal_acquirer_params_terminal_id ON terminal_acquirer_params(terminal_id);

CREATE INDEX IF NOT EXISTS idx_terminal_merchant_id ON terminal(merchant_id);
