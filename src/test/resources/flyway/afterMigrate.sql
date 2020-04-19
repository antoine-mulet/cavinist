insert into regions(id, name, country, version)
values ('2e744843-bf6a-4914-80fd-a802b5a952cb', 'Pomerol', 'France', 0);

insert into regions(id, name, country, version)
values ('66b2eb51-c6ce-49b8-9957-16a2d0b3dece', 'Languedoc-Roussillon', 'France', 0);

insert into regions(id, name, country, version)
values ('f56ba615-45e4-4e81-8b59-b1a6265142a8', 'Pic-Saint-Loup', 'France', 0);

insert into wineries(id, name, region_id, version)
values ('0d60a85e-0b90-4482-a14c-108aea2557aa', 'Petrus', '2e744843-bf6a-4914-80fd-a802b5a952cb', 0);

insert into wineries(id, name, region_id, version)
values ('39240e9f-ae09-4e95-9fd0-a712035c8ad7', 'Domaine de Cazeneuve', '66b2eb51-c6ce-49b8-9957-16a2d0b3dece', 0);

insert into wines(id, name, type, winery_id, region_id, version)
values ('9e4de779-d6a0-44bc-a531-20cdb97178d2', 'Petrus', 0, '0d60a85e-0b90-4482-a14c-108aea2557aa',
        '2e744843-bf6a-4914-80fd-a802b5a952cb', 0);

insert into wines(id, name, type, winery_id, region_id, version)
values ('66a45c1b-19af-4ab5-8747-1b0e2d79339d', 'La Fleur Petrus', 0, '0d60a85e-0b90-4482-a14c-108aea2557aa',
        '2e744843-bf6a-4914-80fd-a802b5a952cb', 0);

insert into wines(id, name, type, winery_id, region_id, version)
values ('bc8250bb-f7eb-4adc-925c-2af315cc4a55', 'Les Calcaires', 0, '39240e9f-ae09-4e95-9fd0-a712035c8ad7',
        'f56ba615-45e4-4e81-8b59-b1a6265142a8', 0);