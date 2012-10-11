package grules

import org.grules.Grules
import org.springframework.dao.DataIntegrityViolationException

class UserController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [userInstanceList: User.list(params), userInstanceTotal: User.count()]
    }

    def create() {
        [userInstance: new User(params)]
    }

    def save() {
        def grules = new Grules()
        def headers = grules.fetchRequestHeaders(request)
        def result = grules.applyGroupRules(UserGrules, [PARAMETERS: params, HEADER: headers])
        if (result.invalidParameters.isEmpty()) {
          def userInstance = new User(result.cleanParameters.PARAMETERS)
          if (!userInstance.save(flush: true)) {
            render(view: "create", model: [userInstance: userInstance])
            return
          }
          flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'),
              userInstance.id])
          redirect(action: "show", id: userInstance.id)
        } else {
          flash.message = message(code: 'grules.errorReport', args: [
            convertParameters(result.cleanParameters),
            convertParameters(result.invalidParameters),
            convertParameters(result.missingRequiredParameters),
            convertParameters(result.notValidatedParameters),
            convertParameters(result.parametersWithMissingDependency)]).replace('\n', '<br>')
            redirect(action: "create")
        }
    }

    private def convertParameters(Map<String, Object> map) {
      if (!map.isEmpty()) {
        map.keySet().inject("") {acc, val -> acc + val + " = " + map[val] + "\n"}
      } else {
        "[:]"
      }
    }

    def show() {
        def userInstance = User.get(params.id)
        if (!userInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect(action: "list")
            return
        }

        [userInstance: userInstance]
    }

    def edit() {
        def userInstance = User.get(params.id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect(action: "list")
            return
        }

        [userInstance: userInstance]
    }

    def update() {
        def userInstance = User.get(params.id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (userInstance.version > version) {
                userInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'user.label', default: 'User')] as Object[],
                          "Another user has updated this User while you were editing")
                render(view: "edit", model: [userInstance: userInstance])
                return
            }
        }

        userInstance.properties = params

        if (!userInstance.save(flush: true)) {
            render(view: "edit", model: [userInstance: userInstance])
            return
        }

    flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "show", id: userInstance.id)
    }

    def delete() {
        def userInstance = User.get(params.id)
        if (!userInstance) {
      flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect(action: "list")
            return
        }

        try {
            userInstance.delete(flush: true)
      flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
      flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
