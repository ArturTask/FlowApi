## Пояснение
Я не до конца понял формулировку задания, так что сделал несколько вариантов:

### Пройдемся по пакетам:
* ```EasyFlow``` - Насколько я понимаю теперь, то, что от меня требовалось,
реализация паттерна pub-sub (под капотом Flow Api)
* ```ProjectReactor``` - реализация паттерна pub-sub (под капотом Project Reactor (Flux))
* ```FlowImpl``` - имплементация самого
 паблишера (+ subscriber, subscriprion),
  буквально implements Flow.Publisher,
 простая логика без настроек стратегий
* ```MyFlow``` - здесь уже попытка написания самопального паблишера 
без наследования, но с использованием многопоточности, отчасти API похоже
на SubmissionPublisher


## Демонстрация работы
Саму демонстрацию можно найти в классе Main:

* ```trySimpleFlowApiImpl();``` - пакет ```FlowImpl```
* ```tryCustomizableFlowApi();``` - пакет ```MyFlow```
* ```tryMainFlowApi();``` - пакет ```EasyFlow```
* ```tryProjectReactor();``` - пакет ```ProjectReactor```

Соответственно раскоментировать один из них (все сразу нежелательно)
```tryMainFlowApi();``` - можно настроить стратегию.

```tryProjectReactor();``` - можно настроить стратегию, для примера взята drop

## Задание

1. Написать простую реализацию паттерна Publish-Subscribe с использованием Java Flow API. 
Предусмотреть возможность настройки стратегии паблишера, применяемые в случае медленных консьюмеров.

2. Выполнить то же условие, что и в первой задаче, только с использованием Project Reactor.