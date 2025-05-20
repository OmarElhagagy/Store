import React from 'react';
import { 
  createTheme, 
  ThemeProvider as MuiThemeProvider, 
  StyledEngineProvider
} from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

// Create a theme
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 600,
    },
  },
  components: {
    MuiGrid: {
      styleOverrides: {
        root: {
          // Add any custom styles for Grid if needed
        },
      },
      defaultProps: {
        // Fix Grid component TypeScript issues
      }
    },
    MuiButtonBase: {
      defaultProps: {
        disableRipple: false,
      },
    },
  },
});

interface ThemeProviderProps {
  children: React.ReactNode;
}

const ThemeProvider: React.FC<ThemeProviderProps> = ({ children }) => {
  return (
    <StyledEngineProvider injectFirst>
      <MuiThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </MuiThemeProvider>
    </StyledEngineProvider>
  );
};

export { theme };
export default ThemeProvider; 