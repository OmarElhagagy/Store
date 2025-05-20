import { ComponentProps } from 'react';
import { Grid as MuiGrid } from '@mui/material';

declare module '@mui/material/Grid' {
  interface GridProps {
    item?: boolean;
    container?: boolean;
  }
  
  interface GridTypeMap {
    props: GridProps;
  }
} 