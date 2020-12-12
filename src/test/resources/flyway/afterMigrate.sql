insert into users(id, login, password)
values ('eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 'user-one',
        '$argon2id$v=19$m=32768,t=5,p=2$jAvGXLy52MdC3L9JP523Hg$XbUUJZCfgVb7SNI7WHDon5UBrmLgwY1OO/SsXLeIXfs'); -- plain text password: Th!sIs@Saf3PwdToHash

insert into users(id, login, password)
values ('c9bff611-1206-4661-924d-5e86f551f430', 'user-two',
        '$argon2id$v=19$m=32768,t=5,p=2$qa+i4Ou8eRerTj2UM3ntbw$+NMcFCDTSlZUP66qUdjhsaMjep4MJgDIIaCHRWCJQyk'); -- plain text password: Th!sIs@noth3rPwdToHash

insert into regions(id, name, country, user_id, version)
values ('2e744843-bf6a-4914-80fd-a802b5a952cb', 'Pomerol', 'France', 'eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 0);

insert into regions(id, name, country, user_id, version)
values ('66b2eb51-c6ce-49b8-9957-16a2d0b3dece', 'Languedoc-Roussillon', 'France',
        'eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 0);

insert into regions(id, name, country, user_id, version)
values ('f56ba615-45e4-4e81-8b59-b1a6265142a8', 'Pic-Saint-Loup', 'France', 'eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 0);

insert into regions(id, name, country, user_id, version)
values ('3db5de72-0932-45da-b377-3bd109379cdf', 'Chianti', 'Italy', 'c9bff611-1206-4661-924d-5e86f551f430', 0);

insert into wineries(id, name, region_id, user_id, version)
values ('0d60a85e-0b90-4482-a14c-108aea2557aa', 'Petrus', '2e744843-bf6a-4914-80fd-a802b5a952cb',
        'eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 0);

insert into wineries(id, name, region_id, user_id, version)
values ('39240e9f-ae09-4e95-9fd0-a712035c8ad7', 'Domaine de Cazeneuve', '66b2eb51-c6ce-49b8-9957-16a2d0b3dece',
        'eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 0);

insert into wineries(id, name, region_id, user_id, version)
values ('42762c71-80f5-4c9b-bb19-7f46739b78e3', 'Querceto di Castellina', '3db5de72-0932-45da-b377-3bd109379cdf',
        'c9bff611-1206-4661-924d-5e86f551f430', 0);

insert into wines(id, name, type, winery_id, region_id, user_id, version)
values ('9e4de779-d6a0-44bc-a531-20cdb97178d2', 'Petrus', 0, '0d60a85e-0b90-4482-a14c-108aea2557aa',
        '2e744843-bf6a-4914-80fd-a802b5a952cb', 'eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 0);

insert into wines(id, name, type, winery_id, region_id, user_id, version)
values ('66a45c1b-19af-4ab5-8747-1b0e2d79339d', 'La Fleur Petrus', 0, '0d60a85e-0b90-4482-a14c-108aea2557aa',
        '2e744843-bf6a-4914-80fd-a802b5a952cb', 'eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 0);

insert into wines(id, name, type, winery_id, region_id, user_id, version)
values ('bc8250bb-f7eb-4adc-925c-2af315cc4a55', 'Les Calcaires', 0, '39240e9f-ae09-4e95-9fd0-a712035c8ad7',
        'f56ba615-45e4-4e81-8b59-b1a6265142a8', 'eb7d367f-d0a6-4906-8584-43bc8b9f5cad', 0);

insert into wines(id, name, type, winery_id, region_id, user_id, version)
values ('1f739c8e-76af-4531-8830-31f2596d63ee', 'Sei', 0, '42762c71-80f5-4c9b-bb19-7f46739b78e3',
        '3db5de72-0932-45da-b377-3bd109379cdf', 'c9bff611-1206-4661-924d-5e86f551f430', 0);