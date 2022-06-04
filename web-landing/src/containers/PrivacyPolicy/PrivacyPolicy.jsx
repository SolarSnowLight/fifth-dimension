import React from "react";
import styles from "./PrivacyPolicy.module.css";

import icon from "../../resources/logo_black.svg";
import { NavLink } from "react-router-dom";

const PrivacyPolicy = () => {
  return (
    <div className={styles["body"]}>
     <NavLink to="/"><h1>На главную</h1>
       </NavLink> 
      <h1>Политика в отношении обработки персональных данных</h1>
      <h2>1. Общие положения</h2>
      Настоящая политика обработки персональных данных составлена в соответствии
      с требованиями Федерального закона от 27.07.2006. №152-ФЗ «О персональных
      данных» и определяет порядок обработки персональных данных и меры по
      обеспечению безопасности персональных данных Григорьева Кристина Игоревна
      (далее – Оператор).
      <ul>
        <li>
          Оператор ставит своей важнейшей целью и условием осуществления своей
          деятельности соблюдение прав и свобод человека и гражданина при
          обработке его персональных данных, в том числе защиты прав на
          неприкосновенность частной жизни, личную и семейную тайну.
        </li>
        <li>
          Настоящая политика Оператора в отношении обработки персональных данных
          (далее – Политика) применяется ко всей информации, которую Оператор
          может получить о посетителях веб-сайта https://kristinakaruna.ru
        </li>
      </ul>
      <h2>2. Основные понятия, используемые в Политике</h2>
      <ul>
        <li>
          Автоматизированная обработка персональных данных – обработка
          персональных данных с помощью средств вычислительной техники;
        </li>
        <li>
          Блокирование персональных данных – временное прекращение обработки
          персональных данных (за исключением случаев, если обработка необходима
          для уточнения персональных данных
        </li>
        <li>
          Веб-сайт – совокупность графических и информационных материалов, а
          также программ для ЭВМ и баз данных, обеспечивающих их доступность в
          сети интернет по сетевому адресу ;
        </li>
        <li>
          Информационная система персональных данных — совокупность содержащихся
          в базах данных персональных данных, и обеспечивающих их обработку
          информационных технологий и технических средств;
        </li>
        <li>
          Обезличивание персональных данных — действия, в результате которых
          невозможно определить без использования дополнительной информации
          принадлежность персональных данных конкретному Пользователю или иному
          субъекту персональных данных;
        </li>
        <li>
          Обработка персональных данных – любое действие (операция) или
          совокупность действий (операций), совершаемых с использованием средств
          автоматизации или без использования таких средств с персональными
          данными, включая сбор, запись, систематизацию, накопление, хранение,
          уточнение (обновление, изменение), извлечение, использование, передачу
          (распространение, предоставление, доступ), обезличивание,
          блокирование, удаление, уничтожение персональных данных;
        </li>
        <li>
          Оператор – государственный орган, муниципальный орган, юридическое или
          физическое лицо, самостоятельно или совместно с другими лицами
          организующие и (или) осуществляющие обработку персональных данных, а
          также определяющие цели обработки персональных данных, состав
          персональных данных, подлежащих обработке, действия (операции),
          совершаемые с персональными данными;
        </li>
        <li>
          Персональные данные – любая информация, относящаяся прямо или косвенно
          к определенному или определяемому Пользователю веб-сайта;
        </li>
        <li>Пользователь – любой посетитель веб-сайта;</li>
        <li>
          Предоставление персональных данных – действия, направленные на
          раскрытие персональных данных определенному лицу или определенному
          кругу лиц;
        </li>
        <li>
          Распространение персональных данных – любые действия, направленные на
          раскрытие персональных данных неопределенному кругу лиц (передача
          персональных данных) или на ознакомление с персональными данными
          неограниченного круга лиц, в том числе обнародование персональных
          данных в средствах массовой информации, размещение в
          информационно-телекоммуникационных сетях или предоставление доступа к
          персональным данным каким-либо иным способом;
        </li>
        <li>
          Трансграничная передача персональных данных – передача персональных
          данных на территорию иностранного государства органу власти
          иностранного государства, иностранному физическому или иностранному
          юридическому лицу;
        </li>
        <li>
          Уничтожение персональных данных – любые действия, в результате которых
          персональные данные уничтожаются безвозвратно с невозможностью
          дальнейшего восстановления содержания персональных данных в
          информационной системе персональных данных и (или) результате которых
          уничтожаются материальные носители персональных данных.
        </li>
      </ul>
      <h2>
        {" "}
        3. Оператор может обрабатывать следующие персональные данные
        Пользователя
      </h2>
      <ul>
        <li>Фамилия, имя, отчество;</li>
        <li>Электронный адрес;</li>
        <li>Номера телефонов;</li>
        <li>Год, месяц, дата и место рождения;</li>
        <li>Фотографии;</li>
        <li>
          Также на сайте происходит сбор и обработка обезличенных данных о
          посетителях (в т.ч. файлов «cookie») с помощью сервисов
          интернет-статистики (Яндекс Метрика и Гугл Аналитика и других).
        </li>
        <li>
          Вышеперечисленные данные далее по тексту Политики объединены общим
          понятием Персональные данные.
        </li>
      </ul>
      <h2>4. Цели обработки персональных данных </h2>
      <ul>
        <li>
          Цель обработки персональных данных Пользователя — информирование
          Пользователя посредством отправки электронных писем; заключение,
          исполнение и прекращение гражданско-правовых договоров; предоставление
          доступа Пользователю к сервисам, информации и/или материалам,
          содержащимся на веб-сайте.
        </li>
        <li>
          Также Оператор имеет право направлять Пользователю уведомления о новых
          продуктах и услугах, специальных предложениях и различных событиях.
          Пользователь всегда может отказаться от получения информационных
          сообщений, направив Оператору письмо на адрес электронной почты
          kristina.berdsk@mail.ru с пометкой «Отказ от уведомлениях о новых
          продуктах и услугах и специальных предложениях».
        </li>
        <li>
          Обезличенные данные Пользователей, собираемые с помощью сервисов
          интернет-статистики, служат для сбора информации о действиях
          Пользователей на сайте, улучшения качества сайта и его содержания.
        </li>
      </ul>
      <h2>5. Правовые основания обработки персональных данных</h2>
      <ul>
        <li>
          Оператор обрабатывает персональные данные Пользователя только в случае
          их заполнения и/или отправки Пользователем самостоятельно через
          специальные формы, расположенные на сайте . Заполняя соответствующие
          формы и/или отправляя свои персональные данные Оператору, Пользователь
          выражает свое согласие с данной Политикой.
        </li>
        <li>
          Оператор обрабатывает обезличенные данные о Пользователе в случае,
          если это разрешено в настройках браузера Пользователя (включено
          сохранение файлов «cookie» и использование технологии JavaScript).
        </li>
      </ul>
      <h2>
        6. Порядок сбора, хранения, передачи и других видов обработки
        персональных данных
      </h2>{" "}
      Безопасность персональных данных, которые обрабатываются Оператором,
      обеспечивается путем реализации правовых, организационных и технических
      мер, необходимых для выполнения в полном объеме требований действующего
      законодательства в области защиты персональных данных.
      <ul>
        <li>
          Оператор обеспечивает сохранность персональных данных и принимает все
          возможные меры, исключающие доступ к персональным данным
          неуполномоченных лиц.
        </li>
        <li>
          Персональные данные Пользователя никогда, ни при каких условиях не
          будут переданы третьим лицам, за исключением случаев, связанных с
          исполнением действующего законодательства.
        </li>
        <li>
          В случае выявления неточностей в персональных данных, Пользователь
          может актуализировать их самостоятельно, путем направления Оператору
          уведомление на адрес электронной почты Оператора с пометкой
          «Актуализация персональных данных».
        </li>
        <li>
          Срок обработки персональных данных является неограниченным.
          Пользователь может в любой момент отозвать свое согласие на обработку
          персональных данных, направив Оператору уведомление посредством
          электронной почты на электронный адрес Оператора с пометкой «Отзыв
          согласия на обработку персональных данных».
        </li>
      </ul>
      <h2>7. Трансграничная передача персональных данных</h2>
      <ul>
        <li>
          Оператор до начала осуществления трансграничной передачи персональных
          данных обязан убедиться в том, что иностранным государством, на
          территорию которого предполагается осуществлять передачу персональных
          данных, обеспечивается надежная защита прав субъектов персональных
          данных.
        </li>
        <li>
          Трансграничная передача персональных данных на территории иностранных
          государств, не отвечающих вышеуказанным требованиям, может
          осуществляться только в случае наличия согласия в письменной форме
          субъекта персональных данных на трансграничную передачу его
          персональных данных и/или исполнения договора, стороной которого
          является субъект персональных данных.
        </li>
      </ul>
      <h2>8. Заключительные положения</h2>
      <ul>
        <li>Пользователь может получить любые разъяснения по интересующим вопросам,
      касающимся обработки его персональных данных, обратившись к Оператору с
      помощью электронной почты .</li>
        <li>В данном документе будут отражены любые
      изменения политики обработки персональных данных Оператором. Политика
      действует бессрочно до замены ее новой версией.</li>
      <li>Актуальная версия
      Политики в свободном доступе расположена в сети Интернет по адресу
      https://kristinakaruna.ru/politica .</li>
      </ul>
    </div>
  );
};

export default PrivacyPolicy;
