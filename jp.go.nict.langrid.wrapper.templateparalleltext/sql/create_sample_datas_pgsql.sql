begin transaction;

copy categories from stdin;
100	農業	agriculture
101	農家	farmer
200	旅行	travel
\.

copy templates from stdin;
1	米の穂の色が変な色になっています。<param id="1" domains="1,2"/>	colors of rice ear turned to strange color<param id="1" domains="1,2"/>
2	料金は往復分です。<param id="1" domains="3"/>	fee is a couple of tickets.<param id="1" domains="3"/>
3	お金は両替しておく。<param id="1" domains="3"/>	at first you should change maney.<param id="1" domains="3"/>
4	農家の朝は早い <param id="1" choices="本当|うそ"/>	farmer''s morning is early.<param id="1" choices="truth|not truth"/>
5	お金も両替しておく。<param id="1" type="text"/>	you should also change maney.<param id="1" type="text"/>
\.

copy categories_templates from stdin;
100	1
200	2
200	3
101	4
200	5
\.

copy words from stdin;
1	昨日	yesturday
2	今日	today
3	明日	tommorrow
4	日本	Japan
5	アメリカ	United States
6	農家	farmer
7	旅行者	traveler
\.

copy domains from stdin;
1	時間	Time
2	場所	Place
3	人	Person
\.

copy domains_words from stdin;
1	1
1	2
1	3
2	4
2	5
3	6
3	7
\.

commit transaction;