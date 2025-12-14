# Исправление Mapper в Keycloak

## Проблема
У вас есть mapper с именем `realm-roles-mapper`, но его тип - **"Audience"**, а не **"Realm Role"**. 
Mapper типа "Audience" добавляет audience в токен, но НЕ добавляет роли пользователя.

## Решение: Создайте правильный mapper

### Шаг 1: Удалите неправильный mapper (опционально)

1. В Keycloak Admin Console: **Clients** → **microservices-client**
2. Вкладка **Client scopes**
3. Откройте **microservices-client-dedicated**
4. Вкладка **Mappers**
5. Найдите mapper `realm-roles-mapper` (тип "Audience")
6. Удалите его (или оставьте, если он нужен для другого)

### Шаг 2: Создайте правильный mapper для ролей

1. В том же месте (Mappers в microservices-client-dedicated)
2. Нажмите **Create mapper** → **By configuration**
3. В списке типов выберите **"Realm roles"** (НЕ "Audience"!)
4. Заполните форму:
   - **Name**: `realm roles` (или любое другое имя)
   - **Mapper type**: Должно быть **"Realm roles"** (автоматически)
   - **Token Claim Name**: `realm_access.roles` (или оставьте по умолчанию)
   - **Add to ID token**: `ON` ✅
   - **Add to access token**: `ON` ✅
   - **Add to userinfo**: `ON` ✅
   - **Multivalued**: `ON` ✅ (чтобы роли были в виде массива)
5. Нажмите **Save**

### Шаг 3: Альтернативный способ (через realm-default)

Если mapper не работает через client scope, создайте его на уровне realm:

1. Перейдите в **Client scopes**
2. Откройте **realm-default**
3. Вкладка **Mappers**
4. Проверьте, есть ли mapper **"realm roles"**
5. Если нет, создайте:
   - **Create mapper** → **By configuration** → **Realm roles**
   - Настройте как указано выше
6. Убедитесь, что этот scope добавлен в ваш клиент:
   - **Clients** → **microservices-client**
   - Вкладка **Client scopes**
   - В разделе **Default Client Scopes** должна быть **realm-default**

### Шаг 4: Проверьте назначение роли пользователю

1. **Users** → выберите вашего пользователя
2. Вкладка **Role mapping**
3. В разделе **Assigned roles** должна быть роль **admin**
4. Если нет - назначьте её:
   - **Assign role** → **Filter by realm roles** → выберите **admin** → **Assign**

### Шаг 5: Получите новый токен

1. **Выйдите** из приложения
2. **Войдите снова**
3. Проверьте токен в консоли браузера:
   ```javascript
   console.log(window.keycloak.tokenParsed.realm_access?.roles);
   ```
   Должна быть роль `admin` в массиве.

## Важно!

- **Mapper type должен быть "Realm roles"**, а не "Audience"
- **Token Claim Name** должен быть `realm_access.roles` (или по умолчанию)
- **Add to access token** должен быть `ON`
- После изменений нужно **выйти и войти снова**, чтобы получить новый токен

