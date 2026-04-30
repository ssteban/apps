# Apps Android (Monorepo)

Este repositorio almacena **todas las aplicaciones Android** (Kotlin) en un mismo lugar.

- Cada carpeta de primer nivel es un **proyecto independiente**.
- La idea es poder trabajar, revisar historial y **deshacer cambios de un proyecto específico sin afectar al resto**.

## Proyectos

- `StudyBotIA/` – App: StudyBotIA
- `app musica/` – App: Música

## Requisitos

- Android Studio (recomendado)
- JDK compatible con Android/Gradle
- Git

## Cómo abrir un proyecto

1. Abre Android Studio
2. **File → Open…**
3. Selecciona la carpeta del proyecto (por ejemplo `StudyBotIA/` o `app musica/`)

> Si usas Gradle desde terminal: entra a la carpeta del proyecto y ejecuta las tareas desde ahí.

## Git: operar por carpeta (proyecto) sin afectar al resto

La clave es **acotar** las operaciones de Git a una ruta usando `-- <ruta>` (pathspec).

### Ver historial de un proyecto (solo su carpeta)

```bash
git log -- "<carpeta-del-proyecto>/"
```

Ejemplos:

```bash
git log -- "StudyBotIA/"
git log -- "app musica/"
```

Ver diferencias solo de una carpeta:

```bash
git diff -- "<carpeta-del-proyecto>/"
```

### Descartar cambios LOCALES (sin commit) de una carpeta

- Quitar cambios del **working tree** y del **staging** (dejar esa carpeta limpia):

```bash
git restore --worktree --staged -- "<carpeta-del-proyecto>/"
```

Solo descartar lo NO staged:

```bash
git restore --worktree -- "<carpeta-del-proyecto>/"
```

Solo sacar del stage (manteniendo modificaciones):

```bash
git restore --staged -- "<carpeta-del-proyecto>/"
```

### Restaurar una carpeta a como estaba en un commit específico (sin tocar el resto)

Esto trae el contenido de la carpeta desde un commit y lo aplica en tu rama actual (luego commiteas solo esa carpeta):

```bash
git restore --source <SHA_O_TAG> -- "<carpeta-del-proyecto>/"
```

Ejemplos:

```bash
git restore --source a1b2c3d -- "StudyBotIA/"
git restore --source a1b2c3d -- "app musica/"
```

Luego confirma solo esa carpeta:

```bash
git add "<carpeta-del-proyecto>/"
git commit -m "Rollback/Restore <carpeta-del-proyecto> to <SHA_O_TAG>"
```

### "Devolver" (undo) un commit SOLO en un proyecto/carpeta

`git revert` no soporta pathspec directamente para revertir “solo una carpeta”.

La alternativa práctica para deshacer el efecto de un commit `<SHA>` **solo en una carpeta** es:

1) Restaurar esa carpeta al estado del **padre** del commit (lo que equivale a “quitar” los cambios de ese commit en esa carpeta)

```bash
git restore --source <SHA>^ -- "<carpeta-del-proyecto>/"
```

2) Commit del rollback solo de esa carpeta:

```bash
git add "<carpeta-del-proyecto>/"
git commit -m "Undo <SHA> changes only in <carpeta-del-proyecto>"
```

### Volver un proyecto a un punto anterior (varios commits) sin afectar otros

1) Encuentra el commit objetivo para esa carpeta:

```bash
git log -- "<carpeta-del-proyecto>/"
```

2) Restaura la carpeta a ese commit:

```bash
git restore --source <SHA_OBJETIVO> -- "<carpeta-del-proyecto>/"
```

3) Confirma solo esa carpeta:

```bash
git add "<carpeta-del-proyecto>/"
git commit -m "Rollback <carpeta-del-proyecto> to <SHA_OBJETIVO>"
```

### Traer una carpeta desde otra rama/commit

```bash
git restore --source <rama-o-sha> -- "<carpeta-del-proyecto>/"
```

Ejemplo (traer `StudyBotIA/` desde `master`):

```bash
git restore --source master -- "StudyBotIA/"
```

## Buenas prácticas (recomendadas)

- Mantén cada app con su propio `README.md` dentro de su carpeta.
- Evita cambios cruzados entre proyectos si no es necesario.

## Licencia

Define aquí la licencia del repositorio si aplica.
