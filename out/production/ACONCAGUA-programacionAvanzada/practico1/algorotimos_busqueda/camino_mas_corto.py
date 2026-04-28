from collections import deque

# Dimensiones de la grilla
N = 3

# Estado inicial y objetivo
inicio = (0, 0)
objetivo = (2, 2)

# Obstáculos
bloqueados = {(1, 1)}

# Movimientos posibles: arriba, abajo, izquierda, derecha
movimientos = [(-1, 0), (1, 0), (0, -1), (0, 1)]

# Cola BFS
cola = deque([inicio])

# Visitados
visitados = set([inicio])

# Para reconstruir el camino
padres = {}

print("Inicio BFS...\n")

# BFS
while cola:
    actual = cola.popleft()
    print(f"Explorando: {actual}")

    # Si llegamos al objetivo → terminar
    if actual == objetivo:
        print("\nObjetivo alcanzado!")
        break

    # Generar vecinos
    for mov in movimientos:
        nx = actual[0] + mov[0]
        ny = actual[1] + mov[1]
        vecino = (nx, ny)

        # Validaciones
        if 0 <= nx < N and 0 <= ny < N:
            if vecino not in bloqueados and vecino not in visitados:
                cola.append(vecino)
                visitados.add(vecino)
                padres[vecino] = actual  # guardo de dónde vine
                print(f"  Agrego a cola: {vecino}")

#mostrar camino
camino = []
nodo = objetivo

while nodo != inicio:
    camino.append(nodo)
    nodo = padres[nodo]

camino.append(inicio)
camino.reverse()

print("\nCamino más corto:")
print(camino)