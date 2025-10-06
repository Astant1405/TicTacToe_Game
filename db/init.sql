--
-- PostgreSQL database dump
--

\restrict Kh99l3FNAxjfJoFeFXqlcFyFLlvU0Z3r5IWi6ekBLrJMJCwNiNoEBFOIrLkwJFY

-- Dumped from database version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.10 (Ubuntu 16.10-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: game; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.game (
    id uuid,
    status character varying(100),
    first_person uuid,
    second_person uuid,
    field_state character varying(9),
    type character varying(9),
    time_of_create timestamp with time zone
);


ALTER TABLE public.game OWNER TO postgres;

--
-- Name: person; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.person (
    id uuid NOT NULL,
    username character varying(100),
    password character varying(100)
);


ALTER TABLE public.person OWNER TO postgres;

--
-- Data for Name: game; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: postgres
--

--
-- Name: person person_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: game first_person; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.game
    ADD CONSTRAINT first_person FOREIGN KEY (first_person) REFERENCES public.person(id);


--
-- Name: game second_person; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.game
    ADD CONSTRAINT second_person FOREIGN KEY (second_person) REFERENCES public.person(id);


--
-- PostgreSQL database dump complete
--

\unrestrict Kh99l3FNAxjfJoFeFXqlcFyFLlvU0Z3r5IWi6ekBLrJMJCwNiNoEBFOIrLkwJFY

