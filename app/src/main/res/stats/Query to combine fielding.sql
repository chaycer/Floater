select player_id, team_id, year, pos, sum(g) as g, sum(gs) as gs, sum(inn_outs) as inn_outs, sum(po) as po, sum(a) as a, sum(e) as e, sum(dp) as dp, sum(pb) as pb, sum(wp) as wp, sum(sb) as sb, sum(cs) as cs, sum(zr)as zr from fielding group by player_id, team_id, year, pos;

select player_id, team_id, year, pos, count(*) from fielding_test group by player_id, team_id, year, pos having count(*) > 1;

select player_id, team_id, year, pos, count(*) from fielding group by player_id, team_id, year, pos having count(*) > 1;

select * from fielding where player_id = 'anderjo01' and team_id = 'BRO' and year = 1898;
select * from fielding_test where player_id = 'anderjo01' and team_id = 'BRO' and year = 1898;

PRAGMA foreign_keys = 0;
DROP TABLE fielding_test;
CREATE TABLE fielding_test (
    player_id TEXT,
    year      INTEGER,
    team_id   TEXT,
    pos       TEXT,
    g         INTEGER,
    gs        NUMERIC,
    inn_outs  NUMERIC,
    po        NUMERIC,
    a         NUMERIC,
    e         NUMERIC,
    dp        NUMERIC,
    pb        NUMERIC,
    wp        NUMERIC,
    sb        NUMERIC,
    cs        NUMERIC,
    zr        NUMERIC
);

insert into fielding_test(
                         player_id,
                         year,
                         team_id,                 
                         pos,
                         g,
                         gs,
                         inn_outs,
                         po,
                         a,
                         e,
                         dp,
                         pb,
                         wp,
                         sb,
                         cs,
                         zr
                     )
select player_id, year, team_id, pos, sum(g) as g, sum(gs) as gs, sum(inn_outs) as inn_outs, sum(po) as po, sum(a) as a, sum(e) as e, sum(dp) as dp, sum(pb) as pb, sum(wp) as wp, sum(sb) as sb, sum(cs) as cs, sum(zr)as zr from fielding group by player_id, team_id, year, pos;
PRAGMA foreign_keys = 1;