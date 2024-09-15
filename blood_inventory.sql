SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

CREATE TABLE blood_inventory (
  blood_type varchar(10) NOT NULL,
  packets_available int(11) NOT NULL,
  Cost int(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO blood_inventory (blood_type, packets_available, Cost) VALUES
('A+', 0, 200),
('A-', 0, 200),
('AB+', 0, 200),
('AB-', 0, 200),
('B+', 0, 200),
('B-', 0, 200),
('O+', 0, 200),
('O-', 0, 200);

ALTER TABLE blood_inventory
  ADD PRIMARY KEY (blood_type);
COMMIT;
