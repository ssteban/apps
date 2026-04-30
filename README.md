# Apps Android (Monorepo)

Este repositorio almacena **todas las aplicaciones Android** (Kotlin) en un mismo lugar.

- Cada carpeta de primer nivel (por ejemplo `app1/`, `app2/`, etc.) es un **proyecto independiente**.
- La idea es poder trabajar, revisar historial y **deshacer cambios de un proyecto específico sin afectar al resto**.

## Estructura

> Ajusta esta sección si cambian los nombres reales de carpetas.

- `/<proyecto-1>` – App Android 1
- `/<proyecto-2>` – App Android 2
- `/<proyecto-n>` – App Android N

## Requisitos

- Android Studio (recomendado)
- JDK compatible con Android/Gradle
- Git

## Cómo abrir un proyecto

1. Abre Android Studio
2. **File → Open…**
3. Selecciona la carpeta del proyecto (por ejemplo `/<proyecto-1>`)

> Si usas Gradle desde terminal: entra a la carpeta del proyecto y ejecuta las tareas desde ahí.

## Git: trabajar por carpeta (proyecto) sin afectar al resto

La clave es **acotar** cualquier operación de Git a una ruta usando:

- `-- <ruta>` al final del comando (pathspec)
- o comandos que reescriben historial, pero **solo** para una carpeta

### Ver historial de un proyecto (solo su carpeta)

```bash
git log -- <carpeta-del-proyecto>/
```

Ver diferencias solo de una carpeta:

```bash
git diff -- <carpeta-del-proyecto>/
```

### Deshacer cambios NO confirmados (working tree) de una carpeta

Descartar cambios locales de archivos modificados en esa carpeta:

```bash
git restore --worktree --staged -- <carpeta-del-proyecto>/
```

- `--worktree`: revierte el working tree
- `--staged`: saca cambios del staging area

Si solo quieres descartar lo NO staged:

```bash
git restore --worktree -- <carpeta-del-proyecto>/
```

Si solo quieres quitar del stage (manteniendo el archivo modificado):

```bash
git restore --staged -- <carpeta-del-proyecto>/
```

### Restaurar una carpeta a como estaba en un commit específico (sin tocar el resto)

Esto "trae" el contenido de la carpeta desde un commit y lo aplica en tu rama actual (generando cambios que luego puedes commitear):

```bash
git restore --source <SHA_O_TAG> -- <carpeta-del-proyecto>/
```

Ejemplo:

```bash
git restore --source a1b2c3d -- proyectoA/
```

Luego confirmas solo esa carpeta:

```bash
git add proyectoA/
git commit -m "Restore proyectoA to a1b2c3d"
```

### Revertir commits que tocaron una carpeta (sin revertir cambios en otras carpetas)

Si quieres **deshacer el efecto** de un commit, pero **solo** para una carpeta:

```bash
git revert <SHA> --no-commit
# luego limita lo revertido a una carpeta descartando lo demás

git restore --staged --worktree -- .
# (opcional) en vez de lo anterior, usa un enfoque más seguro:
# 1) revierte sin commit
# 2) restaura el resto del repo al estado previo
# 3) deja solo la carpeta
```

El método recomendado y más controlable es este (paso a paso):

1) Crea un branch de trabajo:

```bash
git switch -c revert-proyectoA
```

2) Revertir el commit pero sin crear commit aún:

```bash
git revert <SHA> --no-commit
```

3) Descartar TODOS los cambios revertidos excepto la carpeta deseada:

```bash
# descarta todo
git restore --worktree --staged -- .

# vuelve a aplicar el revert SOLO en la carpeta (desde el índice del revert)
# alternativa más simple: repite el revert en limpio y extrae solo la carpeta
```

⚠️ Nota: `git revert` no soporta pathspec de forma directa para "revert solo carpeta".

**Alternativa práctica (recomendada):** restaurar la carpeta desde el commit anterior al que quieres deshacer.

Si quieres "deshacer" el commit `<SHA>` solo en `proyectoA/`, restaura la carpeta usando el padre del commit:

```bash
git restore --source <SHA>^ -- proyectoA/
```

y luego commitea:

```bash
git add proyectoA/
git commit -m "Undo <SHA> changes only in proyectoA"
```

### Sacar (checkout) una carpeta desde otra rama o commit

Traer una carpeta desde `master` (u otra rama) sin tocar el resto:

```bash
git restore --source master -- <carpeta-del-proyecto>/
```

### "Volver" un proyecto a un punto anterior (varios commits) sin afectar otros

1) Encuentra el commit objetivo para esa carpeta:

```bash
git log -- <carpeta-del-proyecto>/
```

2) Restaura la carpeta a ese commit:

```bash
git restore --source <SHA_OBJETIVO> -- <carpeta-del-proyecto>/
```

3) Confirma solo esa carpeta:

```bash
git add <carpeta-del-proyecto>/
git commit -m "Rollback <carpeta-del-proyecto> to <SHA_OBJETIVO>"
```

### Reescribir historial SOLO de una carpeta (avanzado)

Si necesitas eliminar/alterar commits históricos de una carpeta (por ejemplo, limpiar secretos) sin tocar el resto del repo, usa **git-filter-repo** (recomendado por Git) o BFG.

Ejemplo: reescribir historial manteniendo solo una carpeta (útil para extraer un proyecto):

```bash
# requiere instalar git-filter-repo
# https://github.com/newren/git-filter-repo

git filter-repo --path <carpeta-del-proyecto>/ --force
```

⚠️ Esto reescribe historial y puede requerir force-push. Úsalo solo si sabes lo que haces.

## Buenas prácticas (recomendadas)

- Mantén cada app con su propio `README.md` dentro de su carpeta.
- Usa convenciones consistentes para nombres de carpetas.
- Evita cambios cruzados entre proyectos si no es necesario.

## Licencia

Define aquí la licencia del repositorio si aplica.
