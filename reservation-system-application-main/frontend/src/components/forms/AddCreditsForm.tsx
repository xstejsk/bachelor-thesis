import { useState } from "react";
import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import { User } from "../../models/user";
import { UpdateBalanceParams } from "../../service/usersApi";

type AddCreditsFormProps = {
  user: User;
  updateBalance: (params: UpdateBalanceParams) => void;
  closeForm: () => void;
};

const AddCreditsForm = (props: AddCreditsFormProps) => {
  const [amount, setAmount] = useState<number>(0);

  const onSubmit = () => {
    import.meta.env.DEBUG && console.log({ amount });
    props.updateBalance({
      id: props.user.id,
      balance: amount + props.user.balance,
    });
    setAmount(0);
    props.closeForm();
  };

  return (
    <form onSubmit={onSubmit}>
      <FormControl>
        <FormLabel>Částka</FormLabel>
        <div>
          <Input
            // html input attribute
            required
            type="number"
            placeholder="100"
            value={amount}
            onChange={(e) => setAmount(Number(e.target.value))}
            slotProps={{
              input: {
                min: -props.user.balance,
              },
            }}
          />
          {/* Show new balance preview */}
          <FormLabel>
            Nový zůstatek: {Number(props.user.balance) + amount}
          </FormLabel>
        </div>
      </FormControl>
      <Button
        sx={{ mt: 1 /* margin top */, width: "100%" }}
        type="submit"
        disabled={props.user.balance + amount < 0}
      >
        Přidat
      </Button>
    </form>
  );
};

export default AddCreditsForm;
