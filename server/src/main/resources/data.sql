INSERT INTO public.sensors (description,sensor_type,bus_type,bus_address,is_available,created_at) VALUES
	 ('Температура в доме','TEMPERATURE','ONE_WIRE','0x26',true,'2026-10-08 16:44:26'),
	 ('Атмосферное давление','PRESSURE','I2C','0x01',true,'2026-10-08 17:05:26')
	 ON CONFLICT DO NOTHING;

INSERT INTO public.measurements (sensor,measured_value,measured_at,received_at) VALUES
	 (1,23.8,'2026-10-08 16:45:26','2026-10-08 16:46:26'),
	 (1,23.2,'2026-10-08 16:46:26','2026-10-08 16:47:26'),
	 (1,23.1,'2026-10-08 16:47:26','2026-10-08 16:48:26'),
	 (1,22.9,'2026-10-08 16:48:26','2026-10-08 16:49:26'),
	 (1,22.6,'2026-10-08 16:50:26','2026-10-08 16:50:26'),
	 (1,22.7,'2026-10-08 16:51:26','2026-10-08 16:51:26'),
	 (1,22.4,'2026-10-08 16:44:26','2026-10-08 16:45:26')
	 ON CONFLICT DO NOTHING;
