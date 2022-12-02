/**
 * @author: Seok Kyun. Choi. 최석균 (Syaku)
 * @site: http://syaku.tistory.com
 * @since: 2017. 9. 26.
 */

import React from "react";

import locale from "flatpickr/dist/l10n/cs";
import "flatpickr/dist/flatpickr.min.css";
import DatetimePicker, {
  setLocale,
  parseDate,
} from "react-datetimepicker-syaku";

setLocale(locale.cs);

class DemoContainer extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div className="container">
        <p />

        <h3>Date</h3>
        <div>
          <p>{this.state.value.value}</p>
          <DatetimePicker
            onChange={(datetime, value) => console.log(datetime)}
            defaultValue={[parseDate("2013-12-26")]}
            allowInput
          />
        </div>
      </div>
    );
  }
}

export default DemoContainer;
