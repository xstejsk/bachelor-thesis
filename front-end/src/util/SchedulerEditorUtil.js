import { extend, createElement } from "@syncfusion/ej2-base";
function addCategoryAndPriceFields(args) {
  if (document.getElementById("_dialog_wrapper")) {
    document.getElementById("_dialog_wrapper").style.maxHeight = "600px";
  }
  if (args.type === "Editor") {
    if (!args.element.querySelector(".custom-field-row")) {
      let customRow = createElement("div", {
        className: "custom-field-row",
      });
      let row = createElement("div", {
        className: "e-control e-recurrenceeditor e-lib",
      });
      let container = createElement("div", {
        className: "e-editor",
      });
      let capacityWrapper = createElement("div", {
        className: "e-input-wrapper e-form-left",
      });
      let priceWrapper = createElement("div", {
        className: "e-input-wrapper e-form-right",
      });

      let formElement = args.element.querySelector(".e-schedule-form");
      formElement.firstChild.insertBefore(
        customRow,
        formElement.firstChild.firstChild
      );
      let capacityLabel = createElement(
        "label",
        {
          className: "e-float-text e-label-top",
          id: "label_Capacity",
          for: "Capacity",
        },
        "Capacity"
      );
      let priceLabel = createElement(
        "label",
        {
          className: "e-float-text e-label-top",
          id: "label_Price",
          for: "Price",
        },
        "Price"
      );
      priceLabel.innerHTML = "Price";
      capacityLabel.innerHTML = "Capacity";
      let capacityInputWrapper = createElement("div", {
        className:
          "e-control-wrapper e-numeric e-float-input e-input-group e-valid-input",
      });
      let priceInputWrapper = createElement("div", {
        className:
          "e-control-wrapper e-numeric e-float-input e-input-group e-valid-input",
      });
      let inputCapacity = createElement("input", {
        className: "e-field e-input",
        id: "Capacity",
        value: "1",
        title: "Capacity",
        attrs: {
          name: "Capacity",
          role: "spinbutton",
          type: "number",
          min: "1",
          max: "999",
          autocomplete: "off",
          id: "Capacity",
          value: "1",
          ariaLive: "assertive",

          ariaLabel: "label_Capacity",
        },
      });
      let inputPrice = createElement("input", {
        className: "e-field e-input",
        attrs: {
          title: "Price",
          name: "Price",
          role: "spinbutton",
          autocomplete: "off",
          id: "Price",
          value: "1",
          ariaLive: "assertive",
          ariaValueMin: "0",
          ariaValueMax: "999999",
          ariaValueNow: "99",
          ariaLabel: "label_Price",
          step: 50,
          type: "number",
          min: "0",
          max: "999999",
        },
      });
      capacityInputWrapper.appendChild(inputCapacity);
      capacityInputWrapper.appendChild(capacityLabel);
      priceInputWrapper.appendChild(inputPrice);
      priceInputWrapper.append(priceLabel);
      priceWrapper.appendChild(priceInputWrapper);
      capacityWrapper.appendChild(capacityInputWrapper);
      container.appendChild(capacityWrapper);
      container.appendChild(priceWrapper);
      row.appendChild(container);
      customRow.appendChild(row);
    }
  }
}
export default addCategoryAndPriceFields;
