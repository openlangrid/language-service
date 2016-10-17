insert into categories
	(category_id, ja, en) 
values
	('100', '農業', 'agriculture')
	,('101', '農家', 'farmer')
	,('200', '旅行', 'travel');


insert into templates
	(ja, en)
values
	('米の穂の色が変な色になっています。<param id="1" domains="1,2"/>', 'colors of rice ear turned to strange color<param id="1" domains="1,2"/>')
	,('料金は往復分です。<param id="1" domains="3"/>', 'fee is a couple of tickets.<param id="1" domains="3"/>')
	,('お金は両替しておく。<param id="1" domains="3"/>', 'at first you should change maney.<param id="1" domains="3"/>')
	,('農家の朝は早い <param id="1" choices="本当|うそ"/>', 'farmer's morning is early.<param id="1" choices="truth|not truth"/>'),
	,('お金も両替しておく。<param id="1" type="text"/>', 'you should also change maney.<param id="1" type="text"/>');



insert into categories_templates
	(category_id, template_id)
values
	('100', 1)
	,('200', 2)
	,('200', 3)
	,('101', 4)
	,('200', 5);


insert into words
	(ja, en)
values
	('昨日', 'yesturday')
	,('今日', 'today')
	,('明日', 'tommorrow')
	,('日本', 'Japan')
	,('アメリカ', 'United States')
	,('農家', 'farmer')
	,('旅行者', 'traveler');


insert into domains
	(domain_id, ja, en)
values
	('1', '時間', 'Time')
	,('2', '場所', 'Place')
	,('3', '人', 'Person');


insert into domains_words
	(domain_id, word_id)
values
	('1', '1')
	,('1', '2')
	,('1', '3')
	,('2', '4')
	,('2', '5')
	,('3', '6')
	,('3', '7');

