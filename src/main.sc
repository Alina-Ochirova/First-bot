require: slotfilling/slotFilling.sc
module = sys.zb-common
theme: /

    state: 1
        q!: $regex</start>

state: Start
    q!: $regex</start>
    a: Начнём.

state: Hello
    intent: /привет
    @example("Привет")
    @example("Здравствуйте")
    @example("Добрый день")
    a: Привет! Я ваш бот-помощник. Могу помочь с информацией о погоде и курсах валют. Что вас интересует?

state: Bye
    intent: /пока
    @example("Пока")
    @example("До свидания")
    @example("Увидимся")
    a: Пока! Если вам нужна будет помощь, просто напишите.
    
state: Weather
    intent: /погода.*/
    slots:
        city:
            question: В каком городе вы хотите узнать погоду?
            error: Извините, я не знаю такого города. Пожалуйста, повторите.
    script:
        var query = context.message.text.toLowerCase();
        var regex = /погода.*/;
        
        if (regex.test(query)) {
            var city = context.slots.city.value;
            var apiKey = "cb86077081414c69804164940240806";
            var weatherApiUrl = `http://api.weatherapi.com/v1/current.json?key=${apiKey}&q=${city}`;
            
            fetch(weatherApiUrl)
                .then(response => response.json())
                .then(data => {
                    var weather = data.current;
                    Bot.send(`Погода в ${city}: ${weather.temp_c}°C, ${weather.condition.text}`);
                })
                .catch(error => {
                    Bot.send(`Не удалось получить данные о погоде: ${error.message}`);
                });
        } else {
            Bot.setContext('Weather');
            Bot.send('Извините, я не понял ваш запрос о погоде. Попробуйте снова.');
        }

state: Currency
    intent: /курс.*/
    slots:
        currency:
            question: Для какой валюты вы хотите узнать курс? Пример запроса: Доллар
            error: Извините, я не знаю такую валюту. Пожалуйста, повторите.
    script:
        var query = context.message.text.toLowerCase();
        var regex = /курс.*/;
        
        if (regex.test(query)) {
            var currency = context.slots.currency.value;
            var exchangeApiKey = "c36f87ea6f714a1db6ffe4d05dea1633";
            var exchangeApiUrl = `https://openexchangerates.org/api/latest.json?app_id=${exchangeApiKey}`;
            
            fetch(exchangeApiUrl)
                .then(response => response.json())
                .then(data => {
                    var rate = data.rates[currency];
                    Bot.send(`Курс ${currency} на сегодня: ${rate} рублей.`);
                })
                .catch(error => {
                    Bot.send(`Не удалось получить данные о курсе валют: ${error.message}`);
                });
        } else {
            Bot.setContext('Currency');
            Bot.send('Извините, я не понял ваш запрос о курсе валюты. Попробуйте снова.');
        }

state: NoMatch
    event!: noMatch
    a: Извините, я не понял ваш запрос. Попробуйте сформулировать его иначе. Я могу рассказать о погоде и курсах валют.
