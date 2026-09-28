# DsDonate 1.0.0 (Paper 1.21.1)

Меню привилегий DestroyCraft: `/donate` (также `/donat`, `/privileges`, `/привилегии`).

- Красители - привилегии VIP, Luxe, Deluxe, Ultra, Legend, Elite SP: при наведении список
  возможностей, цена и сайт (цвета и тексты сняты со скринов сервера-образца).
- Сундуки - киты привилегий, редстоун - «Закрыть».
- Клик по привилегии пишет в чат ссылку на сайт (`menu.click-message`, `menu.click-url`).

Всё оформление - `plugins/DsDonate/config.yml` (цвета `&a`, `&#RRGGBB`), после правки - `/dsdonate reload`
(право `dsdonate.admin`).

Сборка: `mvn clean package` или GitHub Actions → артефакт `DsDonate`.
