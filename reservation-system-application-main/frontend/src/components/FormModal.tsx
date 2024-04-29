import * as React from "react";
import Modal from "@mui/joy/Modal";
import ModalDialog from "@mui/joy/ModalDialog";
import Typography from "@mui/joy/Typography";
import { ModalClose } from "@mui/joy";
import { DialogTitle } from "@mui/joy";
import { DialogContent } from "@mui/joy";

type FormModalProps = {
  open: boolean;
  setOpen: (open: boolean) => void;
  title: string;
  description: string | undefined;
  challenge: string | undefined;
  form: React.ReactNode;
};

export default function FormModal(props: FormModalProps) {
  return (
    <React.Fragment>
      <Modal open={props.open} onClose={() => props.setOpen(false)}>
        <ModalDialog
          aria-labelledby="basic-modal-dialog-title"
          aria-describedby="basic-modal-dialog-description"
          sx={{ maxWidth: 500 }}
        >
          <ModalClose />
          <DialogTitle>{props.title}</DialogTitle>

          <Typography
            id="basic-modal-dialog-title"
            level="body-md"
            component={"div"}
          >
            {props.challenge && (
              <DialogContent>{props.challenge}</DialogContent>
            )}
          </Typography>
          <Typography level="body-sm" component={"div"}>
            {props.description && (
              <>
                <DialogContent>
                  <Typography
                    id="basic-modal-dialog-title"
                    level="body-xs"
                  ></Typography>
                  {props.description}
                </DialogContent>
              </>
            )}
          </Typography>

          {props.form}
        </ModalDialog>
      </Modal>
    </React.Fragment>
  );
}
