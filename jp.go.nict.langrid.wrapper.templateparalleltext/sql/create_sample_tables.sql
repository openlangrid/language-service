begin;
create table paralleltexts_categories (
	category_id		varchar(256)	primary key
	,"ja"	varchar(256)
	,"en"	varchar(256)
	,"vi"   varchar(256)
);

create table paralleltexts_templates (
	template_id		serial	primary key
	,"ja"	text
	,"en"	text
	,"vi"   text
);

create table paralleltexts_categories_templates (
	category_id		varchar(256)
	,template_id	integer
	,CONSTRAINT fk_category_id FOREIGN KEY (category_id)
      REFERENCES paralleltexts_categories (category_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
	,CONSTRAINT fk_template_id FOREIGN KEY (template_id)
      REFERENCES paralleltexts_templates (template_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION

);

create table paralleltexts_words (
	word_id		serial	primary key
	,"ja"	varchar(256)
	,"en"	varchar(256)
	,"vi"   varchar(256)
);

create table paralleltexts_domains (
	domain_id	integer		primary key
	,"ja"		varchar(256)
	,"en"		varchar(256)
	,"vi"           varchar(256)
);

create table paralleltexts_domains_words (
	domain_id	integer
	,word_id	integer
);
commit;
