# DsMenu 1.0.0 (Paper 1.21.1)

Меню DestroyCraft:

- `/menu` (`/меню`) или ПКМ по предмету-кремню - главное меню: Спавн (`/spawn`), Варпы (позже),
  Кланы (`/clan`), Привилегии (открывает меню привилегий).
- `/donate` (`/donat`, `/privileges`, `/привилегии`) - меню привилегий VIP, Luxe, Deluxe, Ultra, Legend, Elite SP.
- Предмет меню (кремень, модель `custom-model-data: 102`) выдаётся в игровых мирах: при входе через портал,
  при заходе и после возрождения; в лобби (`menu-item.lobby-worlds`) забирается. Выбросить, переложить
  или потерять при смерти его нельзя.

Всё настраивается в `plugins/DsMenu/config.yml`, после правки - `/dsmenu reload` (право `dsmenu.admin`).

Сборка: `mvn clean package` или GitHub Actions → артефакт `DsMenu`.
