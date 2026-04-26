1. Representación del problema

Grilla 3x3 (con obstáculo en (1,1)):

(0,0) (0,1) (0,2)
(1,0) (1,1) (1,2)
(2,0) (2,1) (2,2)

Elementos:

Inicio: (0,0) I
Meta: (2,2) G
Bloqueado: (1,1) X

2. BFS (con cola)

Cola (FIFO)
Visitados
Guardamos el camino
🔹 Nivel 0

Cola:

[(0,0)]

Visitados:

{(0,0)}
Nivel 1

Desde (0,0) → vecinos válidos:

(0,1)
(1,0)

Cola:

[(0,1), (1,0)]

Visitados:

{(0,0), (0,1), (1,0)}

Nivel 2

Expandimos:

Desde (0,1):
(0,2) ✅
(1,1) ❌ 
Desde (1,0):
(2,0) ✅
(1,1) ❌

Cola:

[(0,2), (2,0)]

Visitados:

{(0,0), (0,1), (1,0), (0,2), (2,0)}

Nivel 3
Desde (0,2):
(1,2) ✅
Desde (2,0):
(2,1) ✅

Cola:

[(1,2), (2,1)]

Visitados:

{(0,0), (0,1), (1,0), (0,2), (2,0), (1,2), (2,1)}
Nivel 4
Desde (1,2):
(2,2) OBJETIVO

BFS termina acá (porque encuentra el camino más corto)

3. Camino encontrado

Reconstruimos el camino más corto:

(0,0) → (0,1) → (0,2) → (1,2) → (2,2)
4. Árbol de estados (BFS)
                (0,0)
               /     \
          (0,1)     (1,0)
            |         |
          (0,2)     (2,0)
            |         |
          (1,2)     (2,1)
            |
          (2,2)
📌 5. Representación del camino en la grilla
 I → → ↓
 •   X ↓
 •   • G
💡 6. Idea clave para que lo entiendas bien
BFS explora por niveles
Siempre encuentra el camino más corto
Usa cola (FIFO)
Evita repetir estados con "visitados"