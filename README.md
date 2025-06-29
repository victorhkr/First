# Electrical Field Calculator with JavaFX

This project is a JavaFX-based application for calculating and visualizing electric fields using **Delaunay triangulation** and the **Finite Element Method (FEM)**. It is intended for educational and research purposes, demonstrating numerical simulation and visualization of electric fields on arbitrary two-dimensional domains.

---

## Features

- **Interactive GUI**: Built with JavaFX for a smooth user experience.
- **Delaunay Triangulation**: Generates high-quality meshes for finite element analysis.
- **Finite Element Solver**: Calculates electric field distributions using FEM.
- **Visualization**: View triangulations and calculated field values directly in the UI.
- **Mesh Refinement**: Enhance mesh quality interactively.
- **Matrix Operations**: Utility class for linear algebra operations in FEM.
- **Modular Codebase**: Clear separation between UI, computational logic, and utilities.

---

## Core Classes Overview

- [`EletroEF.java`](src/application/EletroEF.java): Main application/controller for electric field simulation.
- [`DelaunayTriangulator.java`](src/application/DelaunayTriangulator.java): Handles Delaunay triangulation logic.
- [`FEMSolver.java`](src/application/FEMSolver.java): Implements FEM to solve field equations.
- [`Triangulacao.java`](src/application/Triangulacao.java): Core triangulation procedures and geometry management.
- [`MeshRefiner.java`](src/application/MeshRefiner.java): Refines and improves mesh quality.
- [`MeshDrawer.java`](src/application/MeshDrawer.java): Responsible for mesh visualization.
- [`MatrixOperations.java`](src/application/MatrixOperations.java): Matrix/linear algebra utilities.
- [`TriangulationManager.java`](src/application/TriangulationManager.java): Manages the triangulation process.
- [`Triangulo.java`, `Vertice.java`, `Ponto.java`](src/application/): Data classes for geometric primitives.

> View all source files here: [src/application on GitHub](https://github.com/victorhkr/First/tree/main/src/application)

---

## Project Structure

```
First/
├── src/
│   ├── application/      # Core application logic, FEM, triangulation, visualization
│   ├── quicksortpckg/    # Sorting utilities (if present)
│   └── module-info.java  # Java module system config
├── .github/              # GitHub-specific files (actions, templates)
├── README.md
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
└── ...
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
   - Locate the main class in `src/application` (typically named `EletroEF.java`).
   - Click "Run" in your IDE or use:
     ```bash
     javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/module-info.java src/application/*.java
     java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin application.EletroEF
     ```

---

## Usage

- Launch the app and interactively set up your geometry and parameters.
- The application will perform Delaunay triangulation of your domain and apply the FEM solver to compute the electric field.
- Results are visualized for analysis.
- Use mesh refinement and visualization tools as needed.

---

## Contributing

We welcome contributions of all kinds! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines and best practices.

---

## Code of Conduct

Please read our [Code of Conduct](CODE_OF_CONDUCT.md) to keep our community welcoming and respectful.

---

## License

This project is licensed under the MIT License.

---

## Acknowledgements

- JavaFX ([openjfx.io](https://openjfx.io/))
- Computational geometry & FEM resources

---

## Contact

For questions or suggestions, please [open an issue](https://github.com/victorhkr/First/issues) or contact [victorhkr](https://github.com/victorhkr).
