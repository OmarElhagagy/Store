import { ComponentProps } from "react";

declare module "@mui/material/Grid" {
  interface GridProps {
    item?: boolean;
    container?: boolean;
    xs?: boolean | number;
    sm?: boolean | number;
    md?: boolean | number;
    lg?: boolean | number;
    xl?: boolean | number;
  }
}

declare module "@mui/material" {
  interface GridProps {
    item?: boolean;
    container?: boolean;
    xs?: boolean | number;
    sm?: boolean | number;
    md?: boolean | number;
    lg?: boolean | number;
    xl?: boolean | number;
  }
} 