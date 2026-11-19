WITH problemas AS (
 SELECT 'cupos_negativos' problema,id_localidad::text referencia FROM localidad WHERE cupos_disponibles<0
 UNION ALL
 SELECT 'reservas_superan_capacidad',l.id_localidad::text FROM localidad l WHERE l.tipo_localidad='AFORO_GENERAL' AND (SELECT COALESCE(sum(r.cantidad),0) FROM reserva r WHERE r.id_localidad=l.id_localidad AND r.estado IN ('ACTIVA','CONFIRMADA'))>l.capacidad_total
 UNION ALL
 SELECT 'asiento_multiples_reservas_vivas',l.id_localidad::text FROM localidad l WHERE l.tipo_localidad='ASIENTO' AND (SELECT count(*) FROM reserva r WHERE r.id_localidad=l.id_localidad AND r.estado IN ('ACTIVA','CONFIRMADA'))>1
) SELECT * FROM problemas;
