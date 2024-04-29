import React, { useState } from "react";
import { ControllerRenderProps } from "react-hook-form";
import { NewCalendar } from "../models/calendar";

type ImageUploaderProps = {
  field: ControllerRenderProps<NewCalendar, "thumbnail">;
  defaultImage: string | undefined;
};

const ImageUploader = (props: ImageUploaderProps) => {
  const [selectedImage, setSelectedImage] = useState<string | null>(null);

  // Function to handle file selection
  const handleImageSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        if (event.target) {
          setSelectedImage(event.target.result as string);
          props.field.onChange(event.target.result as string);
        }
      };

      reader.readAsDataURL(file);
    }
  };

  // Function to trigger the file input click
  const openFileSelector = () => {
    const fileInput = document.getElementById("imageInput") as HTMLInputElement;
    fileInput.click();
  };

  return (
    <div>
      <input
        type="file"
        id="imageInput"
        accept="image/*"
        style={{ display: "none" }}
        onChange={handleImageSelect}
      />
      <label htmlFor="imageInput">Vyberte obrázek</label>
      {selectedImage ? (
        <img
          src={selectedImage}
          alt="Selected"
          style={{ width: "100%", maxHeight: "300px" }}
        />
      ) : (
        <img
          src={
            props.defaultImage
              ? "data:image/png;base64, " + props.defaultImage
              : "/calendar-default.jpg"
          }
          alt="Placeholder"
          style={{ width: "100%", maxHeight: "300px", cursor: "pointer" }}
          onClick={openFileSelector}
        />
      )}
    </div>
  );
};

export default ImageUploader;
