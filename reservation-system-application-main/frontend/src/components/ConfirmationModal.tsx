import * as React from "react";
import Modal from "@mui/joy/Modal";
import ModalDialog from "@mui/joy/ModalDialog";
import Typography from "@mui/joy/Typography";
import Box from "@mui/material/Box";
import { Button, ModalClose } from "@mui/joy";

type ConfirmationModalProps = {
  open: boolean;
  setOpen: (open: boolean) => void;
  title: string;
  challenge: string;
  onConfirm: () => void;
};

export default function ConfirmationModal(props: ConfirmationModalProps) {
  return (
    <React.Fragment>
      <Modal open={props.open} onClose={() => props.setOpen(false)}>
        <ModalDialog
          aria-labelledby="basic-modal-dialog-title"
          aria-describedby="basic-modal-dialog-description"
          sx={{ maxWidth: 500 }}
        >
          <ModalClose />
          <Typography id="basic-modal-dialog-title" level="h2">
            {props.title}
          </Typography>
          <Typography id="basic-modal-dialog-description">
            {props.challenge}
          </Typography>
          <Box
            sx={{
              display: "flex",
              flexDirection: "row",
              width: "100%",
            }}
            gap={2}
          >
            <Button
              onClick={() => {
                props.setOpen(false);
              }}
              color="neutral"
              variant="solid"
              sx={{
                flexGrow: 1,
              }}
            >
              Ne
            </Button>
            <Button
              onClick={() => {
                props.onConfirm();
                props.setOpen(false);
              }}
              color="danger"
              variant="solid"
              sx={{
                flexGrow: 1,
              }}
            >
              Ano
            </Button>
          </Box>
        </ModalDialog>
      </Modal>
    </React.Fragment>
  );
}
