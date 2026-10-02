package com.m57.hermescontrol.ui.sessions

import com.m57.hermescontrol.data.model.ProjectFolder
import com.m57.hermescontrol.data.model.ProjectInfo
import com.m57.hermescontrol.data.model.SessionInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionWorkspaceGroupsTest {
    private fun project(id: String, name: String, path: String, archived: Boolean = false) =
        ProjectInfo(id = id, name = name, archived = archived, folders = listOf(ProjectFolder(path = path)))

    @Test
    fun `only named backend projects own sessions`() {
        val projects = listOf(project("one", "Workspace", "/code/app"))
        assertEquals("one", namedProjectForSession(SessionInfo(id = "a", cwd = "/code/app/src"), projects)?.id)
        assertNull(namedProjectForSession(SessionInfo(id = "b", cwd = "/code/other"), projects))
        assertNull(namedProjectForSession(SessionInfo(id = "c", cwd = "/code/application"), projects))
        assertNull(namedProjectForSession(SessionInfo(id = "d"), projects))
        assertNull(namedProjectForSession(SessionInfo(id = "e", cwd = "code/app/src"), projects))
    }

    @Test
    fun `nested project wins and archived projects cannot claim rows`() {
        val projects = listOf(
            project("outer", "App", "/code/app"),
            project("inner", "App", "/code/app/ui"),
            project("old", "Old", "/code/app/ui/src", archived = true),
        )
        assertEquals("inner", namedProjectForSession(SessionInfo(id = "a", cwd = "/code/app/ui/src"), projects)?.id)
    }

    @Test
    fun `grouping keeps stable project ids and unassigned rows`() {
        val projects = listOf(project("one", "Same", "/one"), project("two", "Same", "/two"))
        val sessions = listOf(
            SessionInfo(id = "a", cwd = "/two"),
            SessionInfo(id = "b", cwd = "/other"),
            SessionInfo(id = "c", cwd = "/one"),
            SessionInfo(id = "d", cwd = "/two/sub"),
        )
        val groups = groupSessionsByWorkspace(sessions, projects) { it }
        assertEquals(listOf("two", null, "one"), groups.map { it.project?.id })
        assertEquals(listOf(listOf("a", "d"), listOf("b"), listOf("c")), groups.map { group -> group.items.map { it.id } })
    }

    @Test
    fun `no backend project means ordinary chronological list`() {
        assertEquals(emptyList<WorkspaceGroup<SessionInfo>>(), groupSessionsByWorkspace(listOf(SessionInfo(id = "a")), emptyList()) { it })
    }
}
