select player_id, year, team_id, sum(w) as w, sum(l) as l, sum (g) as g, sum (gs) as gs, sum(cg) as cg, sum(sho) as sho, sum(sv) as sv, sum(ipouts) as ipouts, sum(h) as h, sum(er) as er, sum(hr) as hr, sum(bb) as bb, sum(so) as so, sum(baopp) as baopp, NULL as era, sum(ibb) as ibb, sum(wp) as wp, sum(hbp) as hbp, sum(bk) as bk, sum(bfp) as bfp, sum(gf) as gf,sum(r) as r, sum(sh) as sh, sum(sf) as sf, sum(g_idp) as g_idp from pitching group by player_id, team_id, year;

select * from pitching_test where player_id = 'baldwja01' and year = 2005 and team_id = 'BAL';

select * from pitching_test;

PRAGMA foreign_keys = 1;

INSERT INTO pitching_test (
                              player_id,
                              year,
                              team_id,
                              w,
                              l,
                              g,
                              gs,
                              cg,
                              sho,
                              sv,
                              ipouts,
                              h,
                              er,
                              hr,
                              bb,
                              so,
                              baopp,
                              era,
                              ibb,
                              wp,
                              hbp,
                              bk,
                              bfp,
                              gf,
                              r,
                              sh,
                              sf,
                              g_idp
                          )
                          select player_id, year, team_id, sum(w) as w, sum(l) as l, sum (g) as g, sum (gs) as gs, sum(cg) as cg, sum(sho) as sho, sum(sv) as sv, sum(ipouts) as ipouts, sum(h) as h, sum(er) as er, sum(hr) as hr, sum(bb) as bb, sum(so) as so, sum(baopp) as baopp, NULL as era, sum(ibb) as ibb, sum(wp) as wp, sum(hbp) as hbp, sum(bk) as bk, sum(bfp) as bfp, sum(gf) as gf,sum(r) as r, sum(sh) as sh, sum(sf) as sf, sum(g_idp) as g_idp from pitching group by player_id, team_id, year;

PRAGMA foreign_keys = 1;