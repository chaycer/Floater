select * from batting;

select player_id, year, team_id,sum(g) as g, sum(ab) as ab, sum(r) as r, sum(h) as h, sum(double) as double, sum(triple) as triple, sum(hr) as hr, sum(rbi) as rbi, sum(sb) as sb, sum(cs) as cs, sum(bb) as bb, sum(so) as so, sum(ibb) as ibb, sum(hbp) as hbp, sum(sh) as sh, sum(sf) as sf from batting group by player_id, team_id, year;

PRAGMA foreign_keys = 0;

INSERT INTO batting_test (
                        player_id,
                        year,
                        team_id,
                        g,
                        ab,
                        r,
                        h,
                        double,
                        triple,
                        hr,
                        rbi,
                        sb,
                        cs,
                        bb,
                        so,
                        ibb,
                        hbp,
                        sh,
                        sf,
                        g_idp
                    )
                    select player_id, year, team_id,sum(g) as g, sum(ab) as ab, sum(r) as r, sum(h) as h, sum(double) as double, sum(triple) as triple, sum(hr) as hr, sum(rbi) as rbi, sum(sb) as sb, sum(cs) as cs, sum(bb) as bb, sum(so) as so, sum(ibb) as ibb, sum(hbp) as hbp, sum(sh) as sh, sum(sf) as sf,sum(g_idp) as g_idp from batting group by player_id, team_id, year;

PRAGMA foreign_keys = 1;

select *, count(*) from batting group by player_id, team_id, year having count(*) > 1;

select * from batting_test where player_id = 'anderjo01' and year = 1898 and team_id = 'BRO'
