import React, { forwardRef } from 'react';
import { Grid as MuiGrid, GridProps } from '@mui/material';

// Workaround for MUI Grid component TypeScript issues
const Grid = forwardRef<HTMLDivElement, GridProps & { item?: boolean; container?: boolean }>(
  (props, ref) => {
    return <MuiGrid ref={ref} {...props} />;
  }
);

Grid.displayName = 'Grid';

export default Grid; 