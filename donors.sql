CREATE TABLE donors (
  customer_id int(255) NOT NULL,
  name text NOT NULL,
  blood_type text NOT NULL,
  District varchar(50) NOT NULL,
  last_donation_date date DEFAULT NULL,
  next_donation_date date NOT NULL,
  contact_number text DEFAULT NULL,
  Sex varchar(7) NOT NULL,
  Age int(11) NOT NULL,
  Password varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO donors (customer_id, name, blood_type, District, last_donation_date, next_donation_date, contact_number, Sex, Age, Password) VALUES
(1, 'Vishruth', 'A+', 'Bangalore South', '2024-03-24', '2024-05-19', '9380924094', 'Male', 60, '123'),
(2, 'Rahul', 'B-', 'Bangalore North', '2024-03-23', '2024-05-18', '1234567890', 'Male', 22, '123'),
(3, 'Shiva', 'AB-', 'Mysore', '2024-03-23', '2024-05-18', '1122334455', 'Male', 23, '123'),
(4, 'Nara', 'A-', 'Bangalore South', '2000-09-08', '2001-03-08', '1133445566', 'Male', 56, '123');

ALTER TABLE donors
  ADD PRIMARY KEY (customer_id),
  ADD UNIQUE KEY contact_number (contact_number) USING HASH;

ALTER TABLE donors
  MODIFY customer_id int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;
