package com.m57.hermescontrol.ui.sessions

import com.m57.hermescontrol.data.model.ProjectInfo
import com.m57.hermescontrol.data.model.SessionInfo

/** A named gateway project, or the ordinary unassigned history rows. */
data class WorkspaceGroup<T>(
    val project: ProjectInfo?,
    val items: List<T>,
)

/** Group loaded rows only when the gateway returned named projects with folders.
 * Keep group and row order as seen in the paginated history; never infer a project from a title.
 */
fun <T> groupSessionsByWorkspace(
    items: List<T>,
    projects: List<ProjectInfo>,
    sessionOf: (T) -> SessionInfo,
): List<WorkspaceGroup<T>> {
    if (projects.none { !it.isArchived && it.folders.any { folder -> folder.path.isNotBlank() } }) return emptyList()
    val grouped = linkedMapOf<String?, MutableList<T>>()
    val owners = mutableMapOf<String, ProjectInfo>()
    items.forEach { item ->
        val owner = namedProjectForSession(sessionOf(item), projects)
        if (owner != null) owners[owner.id] = owner
        grouped.getOrPut(owner?.id) { mutableListOf() }.add(item)
    }
    return grouped.map { (id, rows) -> WorkspaceGroup(id?.let(owners::get), rows) }
}
