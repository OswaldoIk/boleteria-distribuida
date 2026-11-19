INSERT INTO localidad(nombre,tipo_localidad,codigo_silla,precio,estado)
SELECT 'Palco VIP '||n,'ASIENTO', 'P'||lpad(n::text,2,'0'),250000,'DISPONIBLE' FROM generate_series(1,10) n;
INSERT INTO localidad(nombre,tipo_localidad,capacidad_total,cupos_disponibles,precio,estado)
VALUES ('General Norte','AFORO_GENERAL',100,100,80000,'DISPONIBLE'),('General Sur','AFORO_GENERAL',500,500,50000,'DISPONIBLE');
