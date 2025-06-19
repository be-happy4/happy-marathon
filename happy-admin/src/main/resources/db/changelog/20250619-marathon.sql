
DROP TABLE IF EXISTS public.tbl_game;
CREATE TABLE public.tbl_game (
	game_id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
	game_name varchar NOT NULL,
	web_url varchar(512) NULL,
	register_start_time timestamp(0) NULL,
	register_end_time timestamp(0) NULL,
	game_start_time timestamp(0) NULL,
	district varchar NOT NULL,
	chinese_athletics_level varchar NULL,
	world_athletics_level varchar NULL,
	status varchar NULL,
	pickup_supplies_location varchar NULL,
	create_by varchar(32) NOT NULL,
	create_time timestamp(0) NOT NULL,
	update_by varchar(32) NOT NULL,
	update_time timestamp(0) NOT NULL,
	CONSTRAINT tbl_game_pk PRIMARY KEY (game_id)
);
COMMENT ON TABLE public.tbl_game IS 'Marathon game record';

-- Column comments

COMMENT ON COLUMN public.tbl_game.game_name IS 'Game name';
COMMENT ON COLUMN public.tbl_game.web_url IS 'Official website url';
COMMENT ON COLUMN public.tbl_game.district IS 'World take place district';
COMMENT ON COLUMN public.tbl_game.status IS 'Game status';
COMMENT ON COLUMN public.tbl_game.pickup_supplies_location IS 'Location to pick up supplies';

