# Electrical Field Calculator with JavaFX

This project is a JavaFX-based application for calculating and visualizing electrical fields using **Delaunay triangulation** and the **Finite Element Method (FEM)**. The tool is intended for educational and research purposes, providing an interactive way to explore computational electromagnetics concepts.

---

## Features

- **Interactive GUI**: Built with JavaFX for a smooth user experience.
- **Delaunay Triangulation**: Generates high-quality meshes for finite element analysis.
- **Finite Element Solver**: Calculates electrical field distributions on arbitrary geometries.
- **Visualization**: View triangulations and calculated field values directly in the UI.
- **Modular Codebase**: Clean separation between UI, computational logic, and utilities.

---

## Project Structure

```
First/
├── .classpath
├── .gitignore
├── .project
├── .settings/
├── bin/
├── build.fxbuild
├── src/
│   ├── application/      # JavaFX app logic and GUI controllers
│   ├── quicksortpckg/    # (Presumed) sorting utilities
│   └── module-info.java  # Java module system config
└── README.md
```

---

## Getting Started

### Prerequisites

- Java 11 or newer (JavaFX requires modules)
- JavaFX SDK (download from [https://openjfx.io/](https://openjfx.io/))
- (Recommended) An IDE like IntelliJ IDEA or Eclipse with JavaFX support

### Build & Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/victorhkr/First.git
   cd First
   ```

2. **Open in your IDE** and configure JavaFX libraries if necessary.

3. **Run the application:**
   - Locate the main class in `src/application` (typically named `Main.java` or similar).
   - Click "Run" or use:
     ```bash
     javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/module-info.java src/application/*.java
     java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin application.Main
     ```

---

## Usage

- Launch the app and interactively set up your geometry and parameters.
- The application will perform Delaunay triangulation of your domain and apply the FEM solver to compute the electrical field.
- Results are visualized on the screen for analysis.

---

## Contributing

Contributions are welcome! Please fork the repository and submit pull requests for improvements, bug fixes, or new features.

---

## License

This project is licensed under the MIT License.

---

## Acknowledgements

- JavaFX ([openjfx.io](https://openjfx.io/))
- Computational geometry & FEM resources

---

## Contact

For questions or suggestions, feel free to open an issue or contact [victorhkr](https://github.com/victorhkr).
