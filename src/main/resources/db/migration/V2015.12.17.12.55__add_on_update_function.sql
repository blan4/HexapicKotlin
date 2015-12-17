CREATE OR REPLACE FUNCTION upd_datetime() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
  IF (NEW != OLD) THEN
    new.updated_at = now();
    RETURN NEW;
  END IF;
  return old;
end;
$$;