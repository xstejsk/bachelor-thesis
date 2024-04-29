## Webová aplikace pro evidenci rezervací - manuál pro spuštění

## Popis
Tento projekt byl vytvořen jako bakalářská práce na téma Webová aplikace pro evidenci rezervací. Cílem práce bylo vytvořit webovou aplikaci, která umožní evidenci rezervací a jejich správu. Aplikace je určena primárně pro provozovatele sportovních středisek. Aplikace umožňuje vytvářet, upravovat a mazat události a příslušené rezervace, vytvářet, mazat a upravovat kalendáře událostí a spravovat uživatelské účty.

## Instalace

Před spuštěním aplikace v ostrém provozu je důrazně doporučeno změnit hodnotu `JWT_SECRET=secret` v souboru `docker-compose.yaml`.

### Požadavky

Pro spuštění aplikace je vyžadován nainstalovaný `docker` a `docker-compose`.

###Docker

Aplikaci je možné spustit pomocí `docker-compose` příkazem:

```
docker-compose up
```

Tento příkaz je nutné spustit ve složce, která obsahuje soubor `docker-compose.yaml`

Serverová aplikace bude dostupná na adrese `http://localhost:8080`, klientská aplikace na adrese `http://localhost:8001`. Pokud jsou porty již obsazené, je možné je změnit v souboru `docker-compose.yml`, doporučuji však pokud možno porty uvolnit a aplikaci spustit na původních portech.

### Inicializace databáze
Aplikace po startu automaticky inicializuje databázi a vytvoří administrátorský účet s přihlašovacími údaji:

```
login: admin@admin.com
heslo: admin
```
Heslo je možné změnit v nastavení uživatele.

Databáze je naplněna ukázkovými daty, jedním administrátorským účtem a několika uživatelskými účty. Údaje pro přihlášení k administrátorskému účtu jsou totožné jako údaje uvedeny výše, heslo pro všechny ostatní uživatele zní `heslo`.Pokud jsou ukázková data nežádoucí, upravte prosím soubor `sports-reservation-system-backend\src\main\resources\data.sql` a odstraňte řádky s ukázkovými daty.
Zmíněný soubor však musí obsahovat řádek s vytvořením administrátorského účtu, kde heslo je hashováno pomocí `bcrypt`. Zároveň je nutné, aby soubor obsahoval alespoň jeden záznam v tabulce `location`, lokace totiž nelze vytvářet v aplikaci, ale pouze v databázi.
Při vkládání nových řádků do souboru prosím uveďtě klauzule `ON CONFLICT ...`, aby nedošlo k chybě při opětovném spuštění aplikace.

## API

API serverové aplikace je podrobně popsáno v souboru `sports-reservation-system-backend\src\main\resources\openapi\api.yaml`. Pro zobrazení API je možné použít nástroj [Swagger Editor](https://editor.swagger.io/).

## Licence

Copyright 2023 Radim Stejskal

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

 

